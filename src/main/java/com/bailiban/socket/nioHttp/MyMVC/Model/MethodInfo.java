package com.bailiban.socket.nioHttp.MyMVC.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MethodInfo {
    private Method method;
    private String name;
}
