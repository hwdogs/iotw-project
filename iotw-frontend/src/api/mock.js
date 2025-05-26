import Mock from 'mockjs'
import homeApi from './mockData/home.js'

// 1.拦截的路径 2.方法 2.制作出的假数据
Mock.mock('/api/home/getTableData/', 'get', homeApi.getTableData);