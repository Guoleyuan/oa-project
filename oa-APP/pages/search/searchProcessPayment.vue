<template>
	<view>
		<view v-for="(item,index) in contractList" :key="item.id" class="formCard">
			<uni-card style="box-shadow: 5rpx 5rpx 15rpx #00ffff" title="加工付款单" :extra="item.processContractNo">
				<view class="cardContent">
					<uni-row style="width: 100%;">
						<uni-col :span="8">
							加工货物名称 :
						</uni-col>
						<uni-col :span="16">
							{{item.goodsName}}
						</uni-col>
					</uni-row>
					<uni-row style="width: 100%;">
						<uni-col :span="8">
							本次付款金额 :
						</uni-col>
						<uni-col :span="16">
							{{item.paymentCount}}
						</uni-col>
					</uni-row>
					<uni-row style="width: 100%;">
						<uni-col :span="8">
							加工单价(元/吨) :
						</uni-col>
						<uni-col :span="16">
							{{item.paymentMonthPriceT}}
						</uni-col>
					</uni-row>
				</view>
				<view class="auditStatusGroup">
					<view v-for="(statusItem,statusIndex) in item.processPaymentDirector" :key="statusItem.userId"
						class="auditStatusItem">
						<view>{{statusItem.nickName}}</view>
						<view v-show="statusIndex==0">
							<uni-icons :style="'margin-left:15rpx'"
								:type="statusItem.state == null ? '' : 'checkmarkempty'" size="24" color="#0081ff">
							</uni-icons>
						</view>
						<view v-show="statusIndex==1">
							<uni-icons :style="'margin-left:15rpx'"
								:type="statusItem.state == null ? '' : 'checkmarkempty'" size="24" color="#0081ff">
							</uni-icons>
						</view>
						<view v-show="statusIndex==2">
							<uni-icons :style="'margin-left:15rpx'"
								:type="statusItem.state == null ? '' : 'checkmarkempty'" size="24" color="#0081ff">
							</uni-icons>
						</view>
					</view>
				</view>
				<view slot="actions" class="actionGroup">
					<view class="actionGroupItem" @tap="actionsClick(item)">
						<button class="buttonGroup">详情</button>
					</view>
				</view>
			</uni-card>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				user: {
					userId: '',
					nickName: '',
				},
				searchWord: '',
				current: 1,
				page: 10,
				total: 0,
				contractList: [],
			}
		},
		onLoad(data) {
			//页面加载完成，获取本地存储的用户信息
			const userData = uni.getStorageSync('userInfo');
			if (userData) {
				this.user.userId = userData.userId;
				this.user.nickName = userData.nickName;
			}
			if (JSON.stringify(data) != '{}') {
				this.searchWord = JSON.parse(JSON.stringify(data)).searchWord;
				this.searchContract();
			}
		},
		onNavigationBarButtonTap() {
			uni.switchTab({
				url: "/pages/MainInterface/audit"
			})
		},
		onReachBottom() {
			this.getMoreContract();
		},
		methods: {
			searchContract() {
				this.$request({
					url: '/processPaymentContract/searchDirectorPPC',
					data: {
						current: this.current,
						page: this.page,
						userId: this.user.userId,
						searchWord: this.searchWord,
					}
				}).then(res => {
					if (res.code == 200) {
						if (res.data.records.length == 0) {
							let that = this;
							uni.showModal({
								content: "未搜索到相关数据",
								showCancel: false,
								success(res) {
									if (res.confirm) {
										uni.navigateBack({
											delta: 1
										})
									}
								}
							})
						} else {
							this.total = res.data.total;
							this.contractList = res.data.records;
						}
					}
				}, err => {
					uni.showModal({
						content: "请求服务失败",
						showCancel: false
					})
				})
			},
			getMoreContract() {
				if (this.contractList.length == this.total) {
					uni.showModal({
						content: "暂无更多数据",
						showCancel: false
					})
				} else {
					this.current = this.current + 1,
						// 获取未审批加工付款单
						this.$request({
							url: '/processPaymentContract/searchDirectorPPC',
							data: {
								current: this.current,
								page: this.page,
								userId: this.user.userId,
								searchWord: this.searchWord,
							}
						}).then(res => {
								if (res.code == 200) {
									this.contractList = [...this.contractList, ...res.data.records];
								}
							},
							err => {
								uni.showModal({
									content: "请求服务失败",
									showCancel: false
								})
							})
				}
			},
			actionsClick(item) {
				uni.navigateTo({
					// 普通参数传输
					url: '/pages/detail/processPaymentDetail?contractId=' + item.id
				})
			},
		}
	}
</script>

<style>
	.formCard {
		/* margin-top: 90rpx; */
	}

	.cardContent {
		display: flex;
		align-items: center;
		flex-direction: column;
		justify-content: center;
	}

	.auditStatusGroup {
		width: 100%;
		display: flex;
		align-items: center;
		flex-direction: row;
		justify-content: center;
		border-top: #DCDCDC solid 1rpx;
		margin-top: 20rpx;
	}

	.auditStatusItem {
		width: 100%;
		display: flex;
		align-items: center;
		flex-direction: row;
		justify-content: center;
		margin-top: 20rpx;
	}

	.actionGroup {
		display: flex;
		align-items: center;
		flex-direction: row;
		justify-content: center;
	}

	.actionGroupItem {
		width: 100%;
		display: flex;
		align-items: center;
		flex-direction: row;
		justify-content: center;
		border-top: #DCDCDC solid 1rpx;
	}

	.buttonGroup {
		width: 100%;
		height: 80rpx;
		font-size: 30rpx;
		margin: 20rpx;
		display: flex;
		align-items: center;
		flex-direction: row;
		justify-content: center;
	}
</style>
