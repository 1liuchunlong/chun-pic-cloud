// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** addSpace GET /api/space/add */
export async function addSpaceUsingGet(
  body: API.SpaceAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>('/api/space/add', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** addSpace PUT /api/space/add */
export async function addSpaceUsingPut(
  body: API.SpaceAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>('/api/space/add', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** addSpace POST /api/space/add */
export async function addSpaceUsingPost(
  body: API.SpaceAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>('/api/space/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** addSpace DELETE /api/space/add */
export async function addSpaceUsingDelete(
  body: API.SpaceAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>('/api/space/add', {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** addSpace PATCH /api/space/add */
export async function addSpaceUsingPatch(
  body: API.SpaceAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>('/api/space/add', {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** deleteSpace GET /api/space/delete */
export async function deleteSpaceUsingGet(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/space/delete', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** deleteSpace PUT /api/space/delete */
export async function deleteSpaceUsingPut(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/space/delete', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** deleteSpace POST /api/space/delete */
export async function deleteSpaceUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/space/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** deleteSpace DELETE /api/space/delete */
export async function deleteSpaceUsingDelete(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/space/delete', {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** deleteSpace PATCH /api/space/delete */
export async function deleteSpaceUsingPatch(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/space/delete', {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getSpaceById GET /api/space/get */
export async function getSpaceByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getSpaceByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseSpace_>('/api/space/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** getSpaceVOById GET /api/space/get/vo */
export async function getSpaceVoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getSpaceVOByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseSpaceVO_>('/api/space/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** listSpaceLevel GET /api/space/list/level */
export async function listSpaceLevelUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseListSpaceLevel_>('/api/space/list/level', {
    method: 'GET',
    ...(options || {}),
  })
}

/** listSpaceByPage POST /api/space/list/page */
export async function listSpaceByPageUsingPost(
  body: API.SpaceQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageSpace_>('/api/space/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** listSpaceVOByPage POST /api/space/list/page/vo */
export async function listSpaceVoByPageUsingPost(
  body: API.SpaceQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageSpaceVO_>('/api/space/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** updateSpace POST /api/space/update */
export async function updateSpaceUsingPost(
  body: API.SpaceUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/space/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
