# JHttpDemo
### Java的Json格式网络请求封装，用于POST请求。

### 使用方法：
>具体请求类实现IRequest接口，具体响应的结果类继承BaseRquest。
请求类和响应类中放相应的字段就可以了。
使用时调用JHttp的postJson方法，把请求类和响应类生成的回调包装类作为参数传入就行了。
