## 使用GitHub的 OAuth App 巩固下 OAuth 的掌握

官方文档：https://docs.github.com/en/developers/apps/authorizing-oauth-apps

1.https://github.com/settings/applications/new 进行申请，需要填入
- 应用名称
- 主页URL
- 回调URL

2.根据生成的 Client ID 和 Client Secret 去做一个访问
- 请求URL：https://github.com/login/oauth/authorize?client_id=885000f93a718e3d6ec0&redirect_uri=http://localhost:8081/callback&scope=user&state=1
- 通过这个URL可以跳转到GitHub账户授权的页面，当然，你需要知道自己的账户和密码。

3.会返回这样的链接 http://localhost:8081/callback?code=c2a6f8e99c68f83d178c&state=1
- 我们拿到这个code并处理，通过这个code去获得access token
- POST https://github.com/login/oauth/access_token

4.新建一个provider包来处理第三方的一些事情，隔离不同的业务
- 新建一个GithubProvider，声明其为@Component，一个组件
- 不同于@Controller（路由API的承载者）

5. 根据官方文档，access token有5个参数，简易直接封装对象
- dto.AccessTokenDTO， dto数据传输模型
- 参数大于两个建议封装成对象
- getAccessToken方法在GithubProvider里

6. 现在拿到了accessToken，下一步是 Use the access token to access the API
- 通过 https://api.github.com/user?access_tokrn=${YOUR VALUE} 就可以获得你GitHub账号的信息了
- 为了存储账号相关信息，再创建一个实体类：dto.GithubUser
- getUser方法在GithubProvider里

7.成功返回了user信息！
- 测试时建议使用浏览器打开无痕窗口！

8.实现效果
- GitHub账号授权登录
- 获得GitHub账号的ID和名称
- 显示当前账号名称，没登录不显示