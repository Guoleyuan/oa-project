<template>
    <el-main>
        <!-- 搜索栏目 -->
        <el-form :model="listParm" :inline="true" size="default">
            <el-form-item label="">
                <el-input v-model="listParm.saleContractNo" placeholder="请输入合同编号"></el-input>
            </el-form-item>
            <el-form-item label="">
                <el-input v-model="listParm.saleCompanyName" placeholder="请输入销售方公司名"></el-input>
            </el-form-item>
            <el-form-item label="">
                <el-input v-model="listParm.goodsName" placeholder="请输入货物名称"></el-input>
            </el-form-item>
            <el-form-item label="">
                <el-input v-model="listParm.squeezeSeason" placeholder="请输入榨季"></el-input>
            </el-form-item>
            <el-form-item label="">
                <el-date-picker v-model="listParm.startTime" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD"
                    placeholder="开始时间" style="width:120px" />
            </el-form-item>
            <el-form-item label="">
                <el-date-picker v-model="listParm.endTime" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD"
                    placeholder="截止时间" style="width:120px" />
            </el-form-item>
            <el-form-item>
                <el-button @click="searchBtn" :icon="Search">搜索</el-button>
                <el-button @click="resetBtn" type="danger" plain :icon="Close">重置</el-button>
            </el-form-item>
        </el-form>
        <!-- 表格 -->
        <el-table :data="tableList.list" border stripe size="small" :height="tableHeight" table-layout="auto"
            :fit="true">
            <el-table-column prop="saleContractNo" label="销售单合同编号"></el-table-column>
            <el-table-column prop="customerEnterpriseName" label="销售方公司名">
                <!-- <template #default="scope">
                    <span>{{ scope.row.customer.customerEnterpriseName }}</span>
                </template> -->
            </el-table-column>
            <el-table-column prop="ownCompanyName" label="己方公司名"></el-table-column>
            <el-table-column prop="saleContractTime" label="合同签订时间" :formatter="conversionDate"></el-table-column>
            <el-table-column prop="goodsName" label="销售货物名称"></el-table-column>
            <el-table-column prop="goodsCount" label="销售货物总量"></el-table-column>
            <el-table-column prop="goodsUnit" label="销售货物单位"></el-table-column>
            <el-table-column prop="goodsUnitPrice" label="销售货物单价"></el-table-column>
            <el-table-column prop="goodsTotalPrice" label="销售合同总价钱"></el-table-column>
            <el-table-column prop="paymentMethod" label="结款方式"></el-table-column>
            <el-table-column prop="transportMethod" label="运输方式"></el-table-column>
            <el-table-column label="销售合同照片">
                <template #default="scope">
                    <el-image style="width: 100px; height: 100px"
                        :src="scope.row.contractPhoto == '' ? null : scope.row.contractPhoto"
                        :preview-src-list="scope.row.contractPhotoList" :initial-index="4" fit="cover"
                        preview-teleported="true" />
                </template>
            </el-table-column>
            <el-table-column prop="revenueTime" label="收款时间" :formatter="conversionDate"></el-table-column>
            <el-table-column prop="revenueAmount" label="收款金额"></el-table-column>
            <el-table-column prop="revenuePhoto" label="收款流水单截图">
                <template #default="scope">
                    <el-image style="width: 100px; height: 100px"
                        :src="scope.row.revenuePhoto == '' ? null : scope.row.revenuePhoto"
                        :preview-src-list="scope.row.revenuePhotoList" :initial-index="4" fit="cover"
                        preview-teleported="true" />
                </template>
            </el-table-column>
            <el-table-column prop="revenueBy" label="出纳操作人姓名"></el-table-column>
            <el-table-column prop="squeezeSeason" label="榨季"></el-table-column>
            <el-table-column prop="createBy" label="创建者名称"></el-table-column>
            <el-table-column fixed="right" label="操作" align="center" width="280">
                <template #default="scope">
                    <el-button type="primary" :icon="MoreFilled" size="default" @click="detailBtn(scope.row)">详情
                    </el-button>

                    <el-button type="success" :icon="Upload" size="default" @click="editBtn(scope.row.id)"
                        :disabled="isEdit(scope.row.contractPhoto)">上传
                    </el-button>

                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination @size-change="sizeChange" @current-change="currentChange"
            :current-page.sync="listParm.currentPage" :page-sizes="[10, 20, 40, 80, 100]" :page-size="listParm.pageSize"
            layout="total, sizes, prev, pager, next, jumper" :total="listParm.total" background>
            :pager-count="7">
        </el-pagination>

        <!-- 详情页 -->
        <CashierSaleDetail ref="detailRef"></CashierSaleDetail>

        <!-- 上传页 -->
        <CashierSaleEdit ref="editRef" @refresh="refresh"></CashierSaleEdit>
    </el-main>

</template>

<script lang="ts" setup>
import { Plus, Edit, Delete, Search, Close, Upload, MoreFilled } from "@element-plus/icons-vue";
import useTable from '@/composables/cashier/sale/useTable'
import { conversionDate } from '@/utils/timeFormat'
import CashierSaleDetail from '@/views/cashier/sale/CashierSaleDetail.vue'
import useDetail from "@/composables/cashier/sale/useDetail";
import CashierSaleEdit from "./CashierSaleEdit.vue";
import useEdit from '@/composables/cashier/sale/useEdit'
//与表格相关的操作
const { listParm, tableHeight, tableList, sizeChange, currentChange, searchBtn, resetBtn, getList } = useTable()

//与销售单相关的操作
const { detailRef, detailBtn } = useDetail()

//与上传相关的操作
const { editBtn, editRef, refresh, isEdit } = useEdit(getList)
</script>

<style scoped>

</style>