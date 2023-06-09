const request = (params) => {
	// 显示加载数据的提示
	uni.showLoading({
		title: '加载中',
		mask: true
	})
	return new Promise(function(resolve, reject) {
		// const baseUrl = 'http://localhost:9000';
		// const baseUrl = 'http://120.77.28.123:9000';
		let baseUrl='';
		const baseUrlStorage = uni.getStorageSync('baseUrl');
		if(baseUrlStorage!=null&&baseUrlStorage!=''){
			baseUrl=baseUrlStorage.baseUrl;
		}
		let token_value='';
		const token = uni.getStorageSync('token');
		if(token!=null&&token!=''){
			token_value=token.token;
		}
		uni.request({
			method: params.method,
			data: params.data,
			url:'http://'+baseUrl+':9000' + params.url,
			header: {
                    // 此处是token 可不加，如果加了需要配置后端允许跨域
				'token': token_value
			},
			success(res) {
				setTimeout(function() {
					resolve(res.data)
				}, 500);
			},
			fail(err) {
				reject(err)
			},
			complete() {
				setTimeout(function() {
					uni.hideLoading()
				}, 1000);
 
			}
		})
	})
}
export default request;