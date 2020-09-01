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

### 说下OAuth ？

http://www.ruanyifeng.com/blog/2019/04/oauth-grant-types.html

**简单说，OAuth 就是一种授权机制。数据的所有者告诉系统，同意授权第三方应用进入系统，获取这些数据。系统从而产生一个短期的进入令牌（token），用来代替密码，供第三方应用使用。**

#### 1. 令牌与密码

`令牌（token）`与`密码（password）`的作用是一样的，都可以进入系统，但是有三点差异。

1. 令牌是短期的，到期会自动失效，用户自己无法修改。密码一般长期有效，用户不修改，就不会发生变化。

2. 令牌可以被数据所有者撤销，会立即失效。以上例而言，屋主可以随时取消快递员的令牌。密码一般不允许被他人撤销。

3. 令牌有权限范围（scope），比如只能进小区的二号门。对于网络服务来说，只读令牌就比读写令牌更安全。密码一般是完整权限。

上面这些设计，保证了令牌既可以让第三方应用获得权限，同时又随时可控，不会危及系统安全。这就是 OAuth 2.0 的优点。

注意，只要知道了令牌，就能进入系统。系统一般不会再次确认身份，所以**令牌必须保密，泄漏令牌与泄漏密码的后果是一样的。** 这也是为什么令牌的有效期，一般都设置得很短的原因。

### 2. RFC 6749

> OAuth 引入了一个授权层，用来分离两种不同的角色：客户端和资源所有者。......资源所有者同意以后，资源服务器可以向客户端颁发令牌。客户端通过令牌，去请求数据。
>
> （由于互联网有多种场景，）本标准定义了获得令牌的四种授权方式（authorization grant ）。
>
> - 授权码（authorization-code）
> - 隐藏式（implicit）
> - 密码式（password）：
> - 客户端凭证（client credentials）

注意，不管哪一种授权方式，第三方应用申请令牌之前，都必须先到系统备案，说明自己的身份，然后会拿到两个身份识别码：客户端 ID（client ID）和客户端密钥（client secret）。这是为了防止令牌被滥用，没有备案过的第三方应用，是不会拿到令牌的。

### 3. 第一种授权方式：授权码 authorization code

**授权码（authorization code）方式，指的是第三方应用先申请一个授权码，然后再用该码获取令牌。**

这种方式是最常用的流程，安全性也最高，它适用于那些有后端的 Web 应用。授权码通过前端传送，令牌则是储存在后端，而且所有与资源服务器的通信都在后端完成。这样的前后端分离，可以避免令牌泄漏。


1. A 网站提供一个链接，用户点击后就会跳转到 B 网站，授权用户数据给 A 网站使用。下面就是 A 网站跳转 B 网站的一个示意链接。

   ```javascript
   https://b.com/oauth/authorize?
   	response_type=code&
   	client_id=CLIENT_ID&
   	redirect_uri=CALLBACK_URL&
   	scope=read
   	
   `response_type`参数表示要求返回授权码（code），`client_id`参数让 B 知道是谁在请求。
   `redirect_uri`参数是 B 接受或拒绝请求后的跳转网址。
   `scope`参数表示要求的授权范围（这里是只读）。
   ```

2. 用户跳转后，B 网站会要求用户登录，然后询问是否同意给予 A 网站授权。用户表示同意，这时 B 网站就会跳回`redirect_uri`参数指定的网址。跳转时，会传回一个授权码，就像下面这样。

   ```javascript
   https://a.com/callback?code=AUTHORIZATION_CODE
   
   `code`参数就是授权码。
   ```
   
3. A 网站拿到授权码以后，就可以在后端，向 B 网站请求令牌。

   ```js
   https://b.com/oauth/token?
    client_id=CLIENT_ID&
    client_secret=CLIENT_SECRET&
    grant_type=authorization_code&
    code=AUTHORIZATION_CODE&
    redirect_uri=CALLBACK_URL
   
   `client_id`参数和`client_secret`参数用来让 B 确认 A 的身份（client_secret参数是保密的，因此只能在后端发请求）
   `grant_type`参数的值是`AUTHORIZATION_CODE`，表示采用的授权方式是授权码
   `code`参数是上一步拿到的授权码
   `redirect_uri`参数是令牌颁发后的回调网址。
   ```

4. B 网站收到请求以后，就会颁发令牌。具体做法是向`redirect_uri`指定的网址，发送一段 JSON 数据。

   ```javascript
   {    
     "access_token":"ACCESS_TOKEN",
     "token_type":"bearer",
     "expires_in":2592000,
     "refresh_token":"REFRESH_TOKEN",
     "scope":"read",
     "uid":100101,
     "info":{...}
   }
       
   上面 `JSON` 数据中，`access_token`字段就是令牌，A 网站在后端拿到了。
   ```

### 4. 剩下三种

隐藏式 implicit

- 有些 Web 应用是纯前端应用，没有后端。这时就不能用上面的方式了，必须将令牌储存在前端。**RFC 6749 就规定了第二种方式，允许直接向前端颁发令牌。这种方式没有授权码这个中间步骤，所以称为（授权码）"隐藏式"（implicit）。**

密码式 password

- **如果你高度信任某个应用，RFC 6749 也允许用户把用户名和密码，直接告诉该应用。该应用就使用你的密码，申请令牌，这种方式称为"密码式"（password）。**

凭证式 client credentials

- **适用于没有前端的命令行应用，即在命令行下请求令牌。**