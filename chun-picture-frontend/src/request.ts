import axios from 'axios'
import { message } from 'ant-design-vue'

// 创建 Axios 实例
const myAxios = axios.create({
  baseURL: 'http://localhost:8123',
  timeout: 60000,
  withCredentials: true,
  transformResponse: [
    function (data) {
      // 如果 data 是字符串，使用自定义解析器修复大整数 id 精度丢失
      if (typeof data === 'string') {
        try {
          // 修复大整数 id 精度丢失：将 id 字段的数字转换为字符串
          // 匹配所有可能的 id 字段模式：
          // - "id": 12345678901234567890
          // - "id":12345678901234567890
          // - 'id': 12345678901234567890
          // 注意：需要匹配嵌套对象中的 id，所以使用更宽泛的正则
          // 关键：必须在 JSON.parse 之前处理，因为一旦解析为数字就已经丢失精度了
          const fixedData = data.replace(
            /(["']id["']\s*:\s*)(\d+)(\s*[,}\]])/g,
            (match, prefix, num, suffix) => {
              // 如果数字超过 15 位（超过 Number.MAX_SAFE_INTEGER 的位数），保留为字符串
              // Number.MAX_SAFE_INTEGER = 9007199254740991 (16位)
              // 为了安全，我们处理所有可能的大整数（15位以上）
              // 因为15位数字也可能接近安全整数范围的上限
              if (num.length >= 15) {
                // 检查是否超过安全整数范围
                // 注意：这里不能直接用 Number(num)，因为如果 num 已经是超过安全范围的字符串，
                // Number(num) 会丢失精度。我们需要直接比较字符串长度和值
                // 9007199254740991 是 16 位数字，所以 16 位及以上的数字都需要转换
                if (num.length > 16 || (num.length === 16 && num > '9007199254740991')) {
                  return `${prefix}"${num}"${suffix}`
                }
              }
              return match
            },
          )
          const parsed = JSON.parse(fixedData)

          // 后处理：递归处理所有对象，确保 id 字段如果是大整数则转换为字符串
          const processBigIntIds = (obj: unknown): unknown => {
            if (obj === null || obj === undefined) {
              return obj
            }
            if (Array.isArray(obj)) {
              return obj.map(processBigIntIds)
            }
            if (typeof obj === 'object') {
              const result: Record<string, unknown> = {}
              for (const key in obj) {
                if (key === 'id' && typeof (obj as Record<string, unknown>)[key] === 'number') {
                  // 如果 id 是数字且可能丢失精度，转换为字符串
                  const idNum = (obj as Record<string, unknown>)[key] as number
                  if (idNum > Number.MAX_SAFE_INTEGER || idNum < Number.MIN_SAFE_INTEGER) {
                    // 如果已经丢失精度，无法恢复，但至少可以尝试
                    result[key] = String(idNum)
                  } else {
                    result[key] = idNum
                  }
                } else {
                  result[key] = processBigIntIds((obj as Record<string, unknown>)[key])
                }
              }
              return result
            }
            return obj
          }

          return processBigIntIds(parsed)
        } catch (e) {
          // 如果修复失败，使用原始解析
          console.warn('JSON 解析失败，使用原始解析:', e)
          return JSON.parse(data)
        }
      }
      return data
    },
  ],
})

// 全局请求拦截器
myAxios.interceptors.request.use(
  function (config) {
    // Do something before request is sent
    return config
  },
  function (error) {
    // Do something with request error
    return Promise.reject(error)
  },
)

// 全局响应拦截器
myAxios.interceptors.response.use(
  function (response) {
    const { data } = response
    // 未登录
    if (data.code === 40100) {
      // 不是获取用户信息的请求，并且用户目前不是已经在用户登录页面，则跳转到登录页面
      if (
        !response.request.responseURL.includes('user/get/login') &&
        !window.location.pathname.includes('/user/login')
      ) {
        message.warning('请先登录')
        window.location.href = `/user/login?redirect=${window.location.href}`
      }
    }
    return response
  },
  function (error) {
    // Any status codes that falls outside the range of 2xx cause this function to trigger
    // Do something with response error
    return Promise.reject(error)
  },
)

export default myAxios
