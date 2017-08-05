## 构建微服务

- 构建
	- 服务提供者：provider-user
		- http://localhost:7900/simple/1
	- 服务消费者：consumer-movie
		- http://localhost:7901/movie/1
	- 服务消费者中通过restTemp调用服务提供者提供的服务
		- 如：`User user = this.restTemplate.getForObject("http://localhost:7900/simple/" + id, User.class);`
- 启动服务
- 浏览器访问服务消费者(内部从服务提供者处获取数据)
