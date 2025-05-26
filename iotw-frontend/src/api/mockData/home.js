export default {
    getTableData: () => {
        return Mock.mock({  // 使用 Mock.mock 包裹响应数据[1,3](@ref)
            code: 200,
            data: {
                'tableData|5-10': [  // 生成5-10条随机数据[2](@ref)
                    {
                        'name': '@cword(3,5)手机', // 随机生成中文产品名
                        'todayBuy|100-500': 1,
                        'monthlyBuy|1000-5000': 1,
                        'totalBuy|2000-10000': 1
                    }
                ]
            }
        })
    }
}