<template>
  <el-main>
    <!-- 搜索栏目 -->
    <el-form :model="listParm" :inline="true" size="default">
      <el-form-item label="">
        <el-input v-model="listParm.logisticsContractNo" placeholder="请输入物流单合同编号" style="width: 180px;"> </el-input>
      </el-form-item>
      <el-form-item label="">
        <el-input v-model="listParm.saleContractNo" placeholder="请输入销售单合同编号" style="width: 180px;"></el-input>
      </el-form-item>
      <el-form-item label="">
        <el-input v-model="listParm.squeezeSeason" placeholder="请输入榨季" style="width: 180px;"></el-input>
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
        <el-button type="primary" @click="addBtn" :icon="Plus">新增</el-button>
        <el-button type="primary" @click="searchPigeonholeZero" :icon="Plus">{{ isPigeonhole ? "显示归档数据" : "显示原始数据" }}
        </el-button>
        <el-button type="success" @click="exportOutBtn" :icon="Plus">导出</el-button>
        <el-upload class="moreDeleteButton" name="file" :action=uploadUrl :on-error="uploadFalse"
          :data="{ createBy: userNickNameStore.user.nickName }" :on-success="uploadSuccess" :limit="1" ref="upload"
          accept=".xlsx,.xls" :show-file-list="false" style="margin-left: 4px; margin-right: 5px; margin-top: 2px;">
          <el-button :icon="Upload" type="primary">导入</el-button>
        </el-upload>
        <el-button type="primary" @click="changeOperateStatus"> {{ operateStatus ? "隐藏操作" : "显示操作" }}</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格 -->
    <el-table :data="tableList.list" border stripe size="small" :height="tableHeight" table-layout="auto" :fit="true">
      <el-table-column prop="logisticsContractNo" label="物流单合同编号"></el-table-column>
      <el-table-column prop="saleContractNo" label="销售/加工单合同编号"></el-table-column>
      <el-table-column prop="ownCompanyName" label="己方公司"></el-table-column>
      <!-- <el-table-column prop="goodsName" label="货物名称"></el-table-column> -->
      <el-table-column prop="totalWeight" label="物流合同总重量"></el-table-column>
      <el-table-column prop="goodsUnit" label="货物单位"></el-table-column>
      <el-table-column prop="freight" label="运费"></el-table-column>
      <el-table-column prop="contractPhoto" label="物流合同照片">
        <template #default="scope">
          <el-image style="width: 100px; height: 100px"
            :src="scope.row.contractPhoto == '' ? null : scope.row.contractPhoto"
            :preview-src-list="scope.row.contractPhotoList" fit="cover" :preview-teleported="true" />
        </template>
      </el-table-column>
      <el-table-column prop="logisticsContractTime" label="物流单合同签订时间" :formatter="conversionDate"></el-table-column>
      <el-table-column prop="squeezeSeason" label="榨季"></el-table-column>
      <el-table-column prop="createBy" label="创建者名称"></el-table-column>
      <el-table-column fixed="right" label="操作" align="center" width="480" v-if="operateStatus">
        <template #default="scope">
          <el-button type="primary" size="default" :icon="MoreFilled"
            @click="detailBtn(scope.row.id, scope.row.logisticsContractNo, scope.row.saleContractNo)">详情
          </el-button>
          <el-button :type="scope.row.pigeonhole == 1 ? 'warning' : 'defalut'"
            :icon="scope.row.pigeonhole == 1 ? Hide : View" size="default" @click="changePigeonhole(scope.row.id)">{{
              isPigeonhole ? "归档" :
              "还原"
            }}
          </el-button>
          <el-button :icon="Money" size="default" type="success" @click="openPaymentDialog(scope.row)">付款
          </el-button>
          <el-button type="info" size="default" :icon="Edit" @click="openUpdateDialog(scope.row)"
            :disabled="getUpdateDisabled(scope.row)">
            <el-tooltip effect="dark" :content="scope.row.tips" placement="top-start"
              :disabled="!(scope.row.relationShippingAuditState == 1 ? (scope.row.contractPhoto != null && scope.row.contractPhoto != '' ? true : false) : (scope.row.relationPaymentAuditState == 1 ? (scope.row.contractPhoto != null && scope.row.contractPhoto != '' ? true : false) : (scope.row.relationShippingExistState == 1 ? (scope.row.contractPhoto != null && scope.row.contractPhoto != '' ? true : false) : (scope.row.relationPaymentExistState == 1 ? (scope.row.contractPhoto != null && scope.row.contractPhoto != '' ? true : false) : false))))">
              修改
            </el-tooltip>
          </el-button>
          <el-button type="danger" :icon="Delete" size="default"
            :disabled="(scope.row.relationPaymentExistState == 1 || scope.row.relationShippingExistState == 1)"
            @click="deleteBtn(scope.row.id)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination @size-change="sizeChange" @current-change="currentChange" :current-page.sync="listParm.currentPage"
      :page-sizes="[10, 20, 40, 80, 100]" :page-size="listParm.pageSize" layout="total, sizes, prev, pager, next, jumper"
      :total="listParm.total" background>
      :pager-count="7">
    </el-pagination>

    <!-- 物流详情 -->
    <DetailLogsitics ref="detailRef"></DetailLogsitics>

    <!-- 新增物流单 -->
    <AddLogis ref="addRef" @refresh="refresh"> </AddLogis>

    <!-- 修改物流单弹窗 -->
    <el-dialog class="updateDialog" v-model="updateDialogFlag" :title="isEdit == false ? '补充合同照片' : '修改物流单'" width="850px"
      draggable center :before-close="closeUpdateDialog">
      <ul ref="updateDialogTop" style="overflow: auto;height:600px">
        <el-form :model="addModel" ref="thridFormRef" label-width="80px" size="default" :rules="rules">
          <el-row>
            <el-col :span="12" :offset="0">
              <el-form-item prop="logisticsContractNo" label="物流单合同编号" label-width='150px' label-position="right">
                <el-input v-model="addModel.logisticsContractNo" placeholder="自提请填写销售合同号" :disabled="isEdit">
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" :offset="0">
              <el-form-item prop="upperType" label="物流单类型" label-width='150px' label-position="right">
                <el-radio-group v-model="addModel.upperType" :disabled="isEdit">
                  <el-radio label="0" size="default">加工单</el-radio>
                  <el-radio label="1" size="default">销售单</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>

          </el-row>
          <el-row>
            <el-col :span="12" :offset="0">
              <el-form-item prop="saleContractNo" label="销售/加工单合同号" label-width='150px' label-position="right">
                <el-input v-model="addModel.saleContractNo" placeholder="运往自家仓库请填写'000'" :disabled="isEdit"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" :offset="0">
              <el-form-item prop="logisticsContractTime" label="物流单合同签订时间" label-width='150px' label-position="right">
                <el-date-picker v-model="addModel.logisticsContractTime" type="date" format="YYYY-MM-DD"
                  :disabled="isEdit" value-format="YYYY-MM-DD" placeholder="请选择合同时间" size="default" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12" :offset="0">
              <el-form-item prop="totalWeight" label="物流合同总重量" label-width='150px' label-position="right">
                <el-input v-model="addModel.totalWeight" :disabled="isEdit"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" :offset="0">
              <el-form-item prop="goodsUnit" label="货物单位" label-width='150px' label-position="right">
                <el-select v-model="addModel.goodsUnit" placeholder="请选择货物单位" size="default" :disabled="isEdit">
                  <el-option label="吨" value="吨"></el-option>
                  <el-option label="斤" value="斤"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12" :offset="0">
              <el-form-item prop="freight" label="运费" label-width='150px' label-position="right" :disabled="isEdit">
                <el-input v-model="addModel.freight" :disabled="isEdit"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" :offset="0">
              <el-form-item prop="squeezeSeason" label="榨季" label-width='150px' label-position="right" :disabled="isEdit">
                <el-input v-model="addModel.squeezeSeason" size="default" :disabled="isEdit"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12" :offset="0">
              <el-form-item prop="ownCompanyName" label="己方公司" label-width='150px' label-position="right">
                <el-select v-model="addModel.ownCompanyName" placeholder="请选择己方公司" size="default" :disabled="isEdit">
                  <!-- <el-option label="广西永湘物流有限公司" value="广西永湘物流有限公司"></el-option>
                <el-option label="广西南宁锦泰行工贸有限公司" value="广西南宁锦泰行工贸有限公司"></el-option>
                <el-option label="广西丰沣顺国际物流有限公司" value="广西丰沣顺国际物流有限公司"></el-option> -->
                  <el-option v-for="item in roleData.list" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12" :offset="0">
              <el-form-item  label="实际重量" label-width='150px' label-position="right">
                <el-input v-model="calWeight" disabled></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-form-item prop="contractPhotoList" label="合同照片" label-width='150px' label-position="right">
              <el-upload v-model:file-list="UpdatePhotoData" :action=uploadPhotoUrl list-type="picture-card"
                :on-preview="handlePictureCardPreview" :on-remove="updateHandleRemove"
                :on-success="updateHandlePhotoSuccess">
                <el-icon>
                  <Plus />
                </el-icon>
              </el-upload>
            </el-form-item>
          </el-row>
          <hr>
          <template v-for="(item, index) in addModel.logisticsDetailList" label-position="right" label-width="200px">
            <el-form-item>
              <el-tag size="large" hit>{{ "物流详情单" + (index + 1) }}</el-tag>
            </el-form-item>
            <el-row>
              <el-col :span="12" :offset="0">
                <el-form-item :prop="'logisticsDetailList.' + index + '.logisticsContractNo'" label="物流单合同编号"
                  label-width='150px' label-position="right">
                  <el-input v-model="getLogisticsDetailList" disabled></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12" :offset="0">
                <el-form-item :prop="'logisticsDetailList.' + index + '.upperType'" label="物流详情单类型" label-width='150px'
                  label-position="right" :rules="[
                    { required: true, trigger: ['change'] }]">
                  <el-radio-group v-model="item.upperType" :disabled="isEdit">
                    <el-radio label="1" size="default">采购单</el-radio>
                    <el-radio label="0" size="default">加工单</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>


            </el-row>
            <el-row>
              <el-col :span="12" :offset="0">
                <el-form-item :prop="'logisticsDetailList.' + index + '.purchaseContractNo'" label="采购/加工合同编号"
                  label-width='150px' label-position="right" :rules="[
                    { required: true, trigger: ['change'] }]">
                  <el-input v-model="item.purchaseContractNo" placeholder="若从自家仓库出货请填写'000'" :disabled="isEdit">
                  </el-input>
                </el-form-item>
              </el-col>


              <el-col :span="12" :offset="0">
                <el-form-item :prop="'logisticsDetailList.' + index + '.outboundTime'" label="出库日期" label-width='150px'
                  label-position="right" :rules="[
                    { required: true, trigger: ['change'] }]">
                  <el-date-picker v-model="item.outboundTime" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD"
                    :disabled="isEdit" placeholder="请选择出库日期" size="default" />

                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12" :offset="0">
                <el-form-item :prop="'logisticsDetailList.' + index + '.goodsFactory'" label="取货厂名" label-width='150px'
                  label-position="right" :rules="[
                    { required: true, trigger: ['change'] }]">
                  <el-input v-model="item.goodsFactory" placeholder="自家仓库出货请填写'自家仓库'" :disabled="isEdit">
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12" :offset="0">
                <el-form-item :prop="'logisticsDetailList.' + index + '.licensePlateNumber'" label="车牌号"
                  label-width='150px' label-position="right" :rules="[
                    { required: true, trigger: ['change'] }]">
                  <el-input v-model="item.licensePlateNumber" :disabled="isEdit"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12" :offset="0">
                <el-form-item :prop="'logisticsDetailList.' + index + '.goodsWeight'" label="载货量" label-width='150px'
                  label-position="right" :rules="[
                    { required: true, trigger: ['change'] }]">
                  <el-input v-model="item.goodsWeight" :disabled="isEdit"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12" :offset="0">
                <el-form-item :prop="'logisticsDetailList.' + index + '.goodsUnit'" label="货物单位" label-width='150px'
                  label-position="right" :rules="[
                    { required: true, trigger: ['change'] }]">
                  <el-select v-model="item.goodsUnit" placeholder="请选择货物单位" size="default" :disabled="isEdit">
                    <el-option label="吨" value="吨"></el-option>
                    <el-option label="斤" value="斤"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12" :offset="0">
                <el-form-item :prop="'logisticsDetailList.' + index + '.uploadingWeight'" label="卸货量" label-width='150px'
                  label-position="right" :rules="[
                    { required: true, trigger: ['change'] }]">
                  <el-input v-model="item.uploadingWeight"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12" :offset="0">
                <el-form-item :prop="'logisticsDetailList.' + index + '.unloadingLocation'" label="卸货地点"
                  label-width='150px' label-position="right" :rules="[
                    { required: true, trigger: ['change'] }]">
                  <el-input v-model="item.unloadingLocation" :disabled="isEdit"></el-input>
                </el-form-item>
              </el-col>
              <!-- <el-col :span="12" :offset="0">
              <el-form-item label="" label-width='150px' label-position="right">
                <el-button type="warning" icon="Plus" @click="addLogisticsDetail">添加物流详情单</el-button>
              </el-form-item>
            </el-col> -->
            </el-row>
            <el-row>
              <el-col :span="12" :offset="0">
                <el-form-item :prop="'logisticsDetailList.' + index + '.unitPrice'" label="运输单价" label-width='150px'
                  label-position="right" :rules="[
                    { required: true, trigger: ['change'] }]">
                  <el-input v-model="item.unitPrice" :disabled="isEdit"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12" :offset="0">
                <el-form-item :prop="'logisticsDetailList.' + index + '.calculationMethod'" label="结算方式"
                  label-width='150px' label-position="right" :rules="[
                    { required: true, trigger: ['change'] }]">
                  <el-select v-model="item.calculationMethod" placeholder="请选择计算方式" size="default">
                    <el-option label="装货结算" value="装货结算"></el-option>
                    <el-option label="卸货结算" value="卸货结算"></el-option>
                  </el-select>
                  <el-button v-show="index != 0" @click.prevent="removeInboundItem(1, item)" type="danger"
                    style="margin-top: 5px;" :disabled="isEdit">删除</el-button>
                </el-form-item>
              </el-col>
              <el-col :span="12" :offset="0">
                <el-form-item label="" label-width='150px' label-position="right">
                  <el-button type="warning" icon="Plus" @click="addLogisticsDetail(1)"
                    :disabled="isEdit">添加物流详情单</el-button>
                </el-form-item>
              </el-col>
            </el-row>

            <hr>
          </template>
        </el-form>
      </ul>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="updateLogistics(thridFormRef)">
            <!-- <el-button type="primary"> -->
            确定
          </el-button>
          <el-button @click="closeUpdateDialog">取消</el-button>
        </span>
      </template>
    </el-dialog>
    <!-- 物流付款单 -->
    <el-dialog v-model="addDialogFlag" title="新增物流付款单" width="40%" draggable center :before-close="closeAddDialog">
      <ul ref="addDialogTop" style="overflow: auto;height:120px;padding: 0;">
        <el-form ref="firstFormRef" :rules="firstRules" label-position="right" label-width="120px"
          :model="NewLogisticsPaymentContractData" style="max-width: 98%">
          <el-row justify="center">
            <el-col :span="16">
              <!-- 验证物流合同号是否存在 -->
              <el-form-item label="物流合同编号" prop="logisticsContractNo">
                <el-input v-model="NewLogisticsPaymentContractData.logisticsContractNo" size="large"
                  :suffix-icon="contractExistFlag ? 'Select' : 'CloseBold'" @input="checkLogisticsContractNo"
                  disabled="false" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row justify="center">
            <el-col :span="16">
              <el-form-item label="本次付款金额" prop="paymentCount">
                <el-input v-model="NewLogisticsPaymentContractData.paymentCount" size="large" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </ul>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="sendNewLogisticsPaymentContract(firstFormRef)">
            确定
          </el-button>
          <el-button @click="closeAddDialog">取消</el-button>
        </span>
      </template>
    </el-dialog>
  </el-main>
</template>

<script setup lang="ts">
import useTable from '@/composables/logistics/useTable'
import { Plus, Edit, Delete, Search, Close, MoreFilled, Hide, View, Money, Upload } from "@element-plus/icons-vue";
import { conversionDate } from '@/utils/timeFormat'
import useLogistics from '@/composables/logistics/useLogistics'
import useDetail from '@/composables/logistics/useDetail';
import DetailLogsitics from './DetailLogsitics.vue';
import AddLogis from './AddLogis.vue';
import { exportApi, getDetailLogistics, updateLogisticsApi } from '@/api/logistics';
import { AddLogisticsModel, ExportListParm, LogisticsDetailList } from '@/api/logistics/LogisticsModel';
import { computed, nextTick, onMounted, reactive, ref } from 'vue';
import { ElMessage, FormInstance, FormRules, UploadProps, UploadUserFile } from 'element-plus';
import useInstance from '@/hooks/useInstance';
import { deletePhotoApi } from '@/api/handlePhoto';
import { SelectOwnCompany } from '@/api/customer/CustomerModel';
import { getOwnCompanySelectApi } from '@/api/ownCompany';
import { addNewLogisticsPaymentContractApi, checkLogisticsContractNoApi } from '@/api/logisticsPaymentContract';
import { userStore } from '@/store/nickName'
import { baseUrl, uploadPhotoUrl } from '@/http/config'
const uploadUrl = ref(baseUrl + "/api/logistics/logisContractImportExcel")
const userNickNameStore = userStore()
//表格相关属性
const { listParm, searchBtn, resetBtn, tableList, tableHeight, isPigeonhole, refresh, searchPigeonholeZero, sizeChange, currentChange } = useTable()

//物流单相关属性
const { changePigeonhole, deleteBtn, addRef, addBtn } = useLogistics(refresh)

//详情相关属性
const { detailBtn, detailRef } = useDetail()



//导出表格
const exportOutBtn = async () => {
  exportListParm.saleContractNo = listParm.saleContractNo
  exportListParm.logisticsContractNo = listParm.logisticsContractNo
  exportListParm.squeezeSeason = listParm.squeezeSeason
  exportListParm.startTime = listParm.startTime
  exportListParm.endTime = listParm.endTime
  exportListParm.isPigeonhole = isPigeonhole.value == true ? '1' : '0'
  let res = await exportApi(exportListParm)
  if (res && res.code == 200) {
    const abtn = document.createElement("a");
    abtn.href = baseUrl + "/api/logistics/exportExcel"
    abtn.click();
  }
}

const exportListParm = reactive<ExportListParm>({
  logisticsContractNo: '',
  saleContractNo: '',
  squeezeSeason: '',
  startTime: '',
  endTime: '',
  isPigeonhole: '1'
})
const uploadSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
  if (response.code == 200) {
    ElMessage({
      message: response.msg,
      type: 'success',
    })
    refresh()
  } else {
    ElMessage({
      message: response.msg,
      type: 'error',
      duration: 4000
    })
  }
}

const uploadFalse = () => {
  ElMessage({
    message: response.msg,
    type: 'error',
    duration: 4000
  })
}
const isEditFlag = ref<boolean>(false)

//修改和删除diable操作
const getUpdateDisabled = (row: any) => {
  if (row.relationShippingAuditState == 1) {
    if (row.contractPhoto != null && row.contractPhoto != '') {
      row.tips = "存在已审核的相关海运单，不允许修改！"
      return true;
    }
  } else if (row.relationPaymentAuditState == 1) {
    if (row.contractPhoto != null && row.contractPhoto != '') {
      row.tips = "存在已审核的相关物流付款单，不允许修改！"
      return true;
    }
  } else if (row.relationShippingExistState == 1) {
    if (row.contractPhoto != null && row.contractPhoto != '') {
      row.tips = "存在未审核的相关海运单，请将其删除后重试！"
      return true;
    }
  } else if (row.relationPaymentExistState == 1) {
    if (row.contractPhoto != null && row.contractPhoto != '') {
      row.tips = "存在未审核的相关物流付款单，请将其删除后重试！"
      return true;
    }
  }
  return false;
}



const roleData = reactive<SelectOwnCompany>({
  list: []
})
// 计算属性：总重量
const calWeight = computed(() => {
  let sum = 0;
  addModel.logisticsDetailList.forEach(detail => {
    sum += Number(detail.goodsWeight)
  });
  addModel.totalWeight = sum + ''
  return sum;
});

onMounted(() => {
  loginUserName.value = userNickNameStore.user.nickName;
  getOwnCompanySelectApi().then(res => {
    roleData.list = res.data;
  })
})

// 修改物流单
const addModel = reactive<AddLogisticsModel>({
  id: '',
  upperType: '',  //0:加工单   1：销售单
  logisticsContractNo: '',
  ownCompanyName: '',
  goodsName: '',
  relationPaymentExistState: '',
  relationPaymentAuditState: '',
  relationShippingExistState: '',
  relationShippingAuditState: '',
  saleContractNo: '',
  totalWeight: '',
  goodsUnit: '',
  freight: '',
  contractPhoto: '',
  contractPhotoList: [],
  logisticsContractTime: '',
  squeezeSeason: '',
  createBy: '',
  logisticsDetailList: reactive<LogisticsDetailList[]>([
    {
      id: '',
      upperType: '',   //0：加工单  1：采购单
      logisticsContractNo: '',
      purchaseContractNo: '',
      goodsFactory: '',
      outboundTime: '',
      licensePlateNumber: '',
      calculationMethod: '',
      goodsWeight: '',
      goodsUnit: '',
      unloadingLocation: '',
      unitPrice: '',
      createBy: '',
      uploadingWeight: ''
    }
  ])
})

//修改物流单相关属性和放啊
const updateDialogFlag = ref(false)
const isEdit = ref<boolean>(false)
const thridFormRef = ref<FormInstance>()
const UpdatePhotoData = ref<UploadUserFile[]>([])
const preDeletePhoto = ref<string[]>([])
const { global } = useInstance()
const dialogImageUrl = ref('')
const previewImageFlag = ref(false)
const updateDialogTop = ref<any>()




//物流付款单
const addDialogFlag = ref(false)
const contractExistFlag = ref(false)
const loginUserName = ref("")

// 打开新增物流付款单窗口
const firstFormRef = ref<FormInstance>()
const openAddDialog = () => {
  addDialogFlag.value = true
}
// 打开物流付款单修改窗口
const openPaymentDialog = async (row: any) => {
  addDialogFlag.value = true;
  contractExistFlag.value = true;
  NewLogisticsPaymentContractData.logisticsContractNo = row.logisticsContractNo

}

// 新增物流付款单
const NewLogisticsPaymentContractData = reactive({
  id: '',
  logisticsContractNo: '',
  paymentCount: '',
  createBy: ''
})

// 关闭新增窗口
const closeAddDialog = () => {
  addDialogFlag.value = false;
  firstFormRef.value?.resetFields();
  contractExistFlag.value = false;
}
// 验证物流合同是否存在
const checkLogisticsContractNo = (e: any) => {
  checkLogisticsContractNoApi(e).then(res => {
    contractExistFlag.value = res.data
    if (res.data == false) {
      ElMessage({
        message: '所填物流合同编号不存在，请检查！',
        type: 'error',
        grouping: true,
        duration: 1000
      })
    } else {
      ElMessage({
        message: '验证合法！',
        type: 'success',
      })
    }
  })
}
// 发送新增物流付款单请求
const sendNewLogisticsPaymentContract = async (formEl1: FormInstance | undefined) => {
  if (!formEl1) return
  await formEl1.validate((valid, fields) => {
    if (valid) {
      if (contractExistFlag.value == true) {
        NewLogisticsPaymentContractData.createBy = loginUserName.value;
        console.log(NewLogisticsPaymentContractData);
        addNewLogisticsPaymentContractApi(NewLogisticsPaymentContractData).then(res => {
          if (res.data == 1) {
            ElMessage({
              message: '新增物流付款单成功！',
              type: 'success',
            })
            refresh()
            closeAddDialog();
          }
          else {
            ElMessage({
              message: '新增物流付款单失败！',
              type: 'error',
              duration: 4000
            })
          }
        })
      } else {
        ElMessage({
          message: '不存在相应物流合同，请检查！',
          type: 'error',
          duration: 4000
        })
      }
    } else {
      ElMessage({
        message: '表单验证未通过，请检查！',
        type: 'error',
        duration: 4000
      })
    }
  })
}






// 关闭修改采购单窗口
const closeUpdateDialog = () => {
  updateDialogFlag.value = false;
  thridFormRef.value?.resetFields();
  UpdatePhotoData.value = [];
  preDeletePhoto.value = [];
  // if (UpdatePurchaseContractData.contractPhotoArray.length != 0) {
  //   UpdatePurchaseContractData.contractPhotoArray.map((item) => {
  //     deletePhotoApi(item);
  //   });
  //   UpdatePurchaseContractData.contractPhotoArray = [];
  // }
}
// 打开修改采购单窗口
const openUpdateDialog = async (row: AddLogisticsModel) => {
  //判断是否只是可以编辑照片
  if (row.relationPaymentExistState == '1' || row.relationShippingExistState == '1') {
    console.log("只能编辑图片")
    isEdit.value = true
  } else {
    console.log("都可以编辑")
    isEdit.value = false
  }
  console.log("是否可以修改所有的" + isEdit.value)
  updateDialogFlag.value = true;
  for (let i = 0; i < row.contractPhotoList.length; i++) {
    // 当一张图片都没上传是长度为1，但内容为null，因此需要进行判断
    if (row.contractPhotoList[i] != null && row.contractPhotoList[i] != '') {
      UpdatePhotoData.value.push({
        name: i + '',
        url: row.contractPhotoList[i],
      });
    }
  }
  let res = await getDetailLogistics(row.id)
  if (res && res.code == 200) {
    row.logisticsDetailList = res.data
  }
  row.logisticsDetailList.forEach((item) => {
    item.upperType = String(item.upperType)
  })
  // 赋值必须要在窗口显示后，否则会被认定为初始值
  nextTick(async () => {

    global.$objCopy(row, addModel)
    addModel.upperType = String(row.upperType)
    // 当一张图片都没上传是长度为1，但内容为null，不将null清除则会后续传输时会带上null，出现错误
    if (row.contractPhotoList[0] == null || row.contractPhotoList[0] == '') {
      addModel.contractPhotoList.splice(0, 1);
    }
    addModel.createBy = row.createBy
  })
}
// 处理照片预览
const handlePictureCardPreview: UploadProps['onPreview'] = (uploadFile) => {
  dialogImageUrl.value = uploadFile.url!
  previewImageFlag.value = true
}
// 更新窗口照片移除后发送请求后台删除照片
const updateHandleRemove: UploadProps['onRemove'] = (uploadFile, uploadFiles) => {
  console.log(uploadFile, uploadFiles);
  if (addModel.contractPhotoList.indexOf(uploadFile.url!) == -1) {
    addModel.contractPhotoList.splice(addModel.contractPhotoList.indexOf(uploadFile.response.data), 1);
    console.log("移出照片数据组");
    deletePhotoApi(uploadFile.response.data);
    console.log("删除已上传照片");
  } else {
    preDeletePhoto.value.push(uploadFile.url!);
    console.log("加入预删除照片组");
  }
}
// 更新窗口上传照片成功后加入数组
const updateHandlePhotoSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
  if (response.code == 200) {
    addModel.contractPhotoList.push(response.data);
    console.log(addModel.contractPhotoList);
    console.log("加入照片数据组");
  }
}

const getLogisticsDetailList = computed(() => {
  return addModel.logisticsContractNo
})
// 删除入厂单
const removeInboundItem = (flag: number, item: LogisticsDetailList) => {
  if (flag == 0) {
    const index = addModel.logisticsDetailList.indexOf(item)
    if (index !== -1) {
      addModel.logisticsDetailList.splice(index, 1)
    }
  } else if (flag == 1) {
    const index = addModel.logisticsDetailList.indexOf(item)
    if (index !== -1) {
      addModel.logisticsDetailList.splice(index, 1)
    }
  }
}
// 添加物流详情单
const addLogisticsDetail = (flag: number) => {
  // flag为0则是添加，为1则是修改
  if (flag == 0) {
    addModel.logisticsDetailList.push({
      id: '',
      upperType: '',   //0：加工单  1：采购单
      logisticsContractNo: '',
      purchaseContractNo: '',
      goodsFactory: '',
      outboundTime: '',
      licensePlateNumber: '',
      goodsWeight: '',
      goodsUnit: '',
      unloadingLocation: '',
      unitPrice: '',
      createBy: '',
      calculationMethod: '',
      uploadingWeight: ''
    })
  } else if (flag == 1) {
    addModel.logisticsDetailList.push({
      id: '',
      upperType: '',   //0：加工单  1：采购单
      logisticsContractNo: '',
      purchaseContractNo: '',
      goodsFactory: '',
      outboundTime: '',
      licensePlateNumber: '',
      goodsWeight: '',
      goodsUnit: '',
      unloadingLocation: '',
      unitPrice: '',
      createBy: '',
      calculationMethod: '',
      uploadingWeight: ''
    })
  }
}

const updateLogistics = async (formEl1: FormInstance | undefined) => {
  thridFormRef.value?.validate(async (avlid) => {
    if (avlid) {
      console.log("确认修改")
      for (let i = 0; i < preDeletePhoto.value.length; i++) {
        // 删除修改的照片
        deletePhotoApi(preDeletePhoto.value[i]);
        addModel.contractPhotoList.splice(addModel.contractPhotoList.indexOf(preDeletePhoto.value[i]), 1);
        console.log("删除修改照片" + i);
      }
      let res = await updateLogisticsApi(addModel)
      if (res && res.code == 200) {
        ElMessage({
          message: '修改成功！',
          type: 'success',
        })
        updateDialogFlag.value = false;
        thridFormRef.value?.resetFields();
        UpdatePhotoData.value = [];
        preDeletePhoto.value = [];
        refresh()
      } else {
        ElMessage({
          message: '修改失败！',
          type: 'error',
        })
      }
    }
  })
}

//表单校验规则
const firstRules = reactive<FormRules>({
  logisticsContractNo: [
    { required: true, trigger: ['change'] }
  ],
  paymentCount: [
    { required: true, trigger: ['change'] }
  ],
})

//表单验证规则
const rules = reactive({
  logisticsContractNo: [
    {
      required: true,
      trigger: "change",
      message: "请输入物流单号",
    },
  ],
  upperType: [
    {
      required: true,
      trigger: "change",
      message: "请选择类型",
    },
  ],
  saleContractNo: [
    {
      required: true,
      trigger: "change",
      message: "请输入销售/加工单号",
    },
  ],
  logisticsContractTime: [
    {
      required: true,
      trigger: "change",
      message: "请输入物流单合同签订时间",
    },
  ],
  totalWeight: [
    {
      required: true,
      trigger: "change",
      message: "请输入物流合同总重量",
    },
  ],
  goodsUnit: [
    {

      trigger: "change",
      message: "请输入货物单位",
    },
  ],
  freight: [
    {
      required: true,
      trigger: "change",
      message: "请输入运费",
    },
  ],
  squeezeSeason: [
    {
      required: true,
      trigger: "change",
      message: "请输入榨季",
    },
  ],
})

//批量导入
const importSuccess = () => {
  ElMessage({
    message: '修改成功！',
    type: 'success',
  })
}


const operateStatus = ref<boolean>(true)
//改变operateStatus
const changeOperateStatus = () => {
  operateStatus.value = !operateStatus.value
}

</script>

<style scoped ></style>  