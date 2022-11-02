import http from "@/http";
import { moreDeleteModel } from "./PurchaseContractModel";

//分页获取采购单
export const getTPurchaseContractDataApi = (currentPage: number, pageSize: number) => {
    return http.get("purchaseContract/getTPurchaseContractData", {
        current: currentPage,
        page: pageSize,
    })
}

//分页获取采购单
export const getFPurchaseContractDataApi = (currentPage: number, pageSize: number) => {
    return http.get("purchaseContract/getFPurchaseContractData", {
        current: currentPage,
        page: pageSize,
    })
}

// 分页查询采购单
export const searchPurchaseContractApi = (currentPage: number, pageSize: number, searchWord: string) => {
    return http.get("purchaseContract/searchPurchaseContract", {
        current: currentPage,
        page: pageSize,
        searchWord: searchWord
    })
}

// 删除单笔采购单
export const deleteOnePurchaseContractApi = (id: number) => {
    return http.delete(`purchaseContract/deleteOnePurchaseContract/${id}`)
}

// 删除多笔采购单
export const deleteMorePurchaseContractApi = (pram: moreDeleteModel) => {
    return http.post("purchaseContract/deleteMorePurchaseContract", pram)
}

//修改采购单归档状态
export const setPurchaseContractPigeonholeApi = (id: number, pigeonhole: number) => {
    return http.get("purchaseContract/setPurchaseContractPigeonhole", {
        id: id,
        pigeonhole: pigeonhole
    })
}

