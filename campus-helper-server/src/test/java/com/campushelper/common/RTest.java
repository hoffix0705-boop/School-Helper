package com.campushelper.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RTest {

    @Test
    void ok_shouldReturnSuccess() {
        R<String> result = R.ok("data");
        assertEquals(200, result.getCode());
        assertEquals("success", result.getMsg());
        assertEquals("data", result.getData());
    }

    @Test
    void fail_shouldReturnError() {
        R<Void> result = R.fail("错误消息");
        assertEquals(500, result.getCode());
        assertEquals("错误消息", result.getMsg());
    }
}
