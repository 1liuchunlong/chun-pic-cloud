package com.chunchun.chunpicturebackend.common;

import lombok.Data;

import java.io.Serializable;
@Data
public class DeleteRequest implements Serializable {
    /**
     * ID
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
