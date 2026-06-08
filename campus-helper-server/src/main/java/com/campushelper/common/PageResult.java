package com.campushelper.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页返回体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private long page;
    private long pageSize;
    private long total;
    private List<T> records;

    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(
                page.getCurrent(),
                page.getSize(),
                page.getTotal(),
                page.getRecords()
        );
    }
}
