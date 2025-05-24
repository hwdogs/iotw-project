import axios from 'axios'
import {ElMessage} from "element-plus";

const authItemName = "access_token";

const defaultFailure = (message, code, url) => {
    console.warn(`请求地址: ${url}, 状态码: ${code}, 错误信息: ${message}`)
    ElMessage.warning(message)
}

const defaultError = (err) => {
    console.error(err)
    ElMessage.warning('发生了一些错误，请联系管理员')
}

function takeAccessToken() {
    const str = localStorage.getItem(authItemName) || localStorage.getItem(defaultFailure);
    if (!str) return null
    const authObj = JSON.parse(str)
    if (authObj.expire <= Date.now()) {
        deleteAccessToken()
        ElMessage.success('登录状态已过期, 请重新登录')
        return null
    }
    return authObj
}

function deleteAccessToken() {
    localStorage.removeItem(authItemName)
    sessionStorage.removeItem(authItemName)
}

function accessHeader() {
    const token = takeAccessToken();
    return token ? {Authorization: `Bearer ${takeAccessToken()}`} : {}
}

function storeAccessToken(token, remember, expire) {
    const authObj = {token: token, expire: expire}
    const str = JSON.stringify(authObj)
    if(remember) {
        localStorage.setItem(authItemName, str)
    }else {
        sessionStorage.setItem(authItemName, str)
    }
}

function internalPost(url, data, header, success, failure, error = defaultError) {
    axios.post(url, data, {headers: header})
        .then(({data}) => {
            if (data.code === 200) {
                success(data.data)
            }else {
                failure(data.msg || data.message, data.code, url)
            }
        }).catch(err => {
            // 处理网络级错误（如401状态码）
            if (err.response) {
                // 从错误响应中提取服务器返回的信息
                const res = err.response.data
                failure(res.message, res.code, url)
            } else {
                error(err)
            }
        })
}

function internalGet(url, header, success, failure, error = defaultError) {
    axios.get(url, {headers: header}).then(({data}) => {
        if (data.code === 200) {
            success(data.data)
        }else {
            failure(data.msg || data.message)
        }
    }).catch(err => error(err))
}

function get(url, success, failure = defaultFailure) {
    internalGet(url, accessHeader(), success, failure)
}

function post(url, data, success, failure = defaultError) {
    internalPost(url, data, accessHeader(), success, failure)
}

function login(username, password, remember, success, failure = defaultFailure) {
    internalPost('/api/auth/login', {
        username : username,
        password: password,
    },{
        'Content-Type': 'application/x-www-form-urlencoded'
    },(data) => {
        storeAccessToken(data.token, remember, data.expire)
        ElMessage.success(`登陆成功, 欢迎${data.username}莅临我们的系统`)
        success(data)
    }, failure)
}

function logout(success, failure = defaultFailure) {
    get('/api/auth/logout', () => {
        deleteAccessToken()
        ElMessage.success('退出登录成功，欢迎您再次使用')
        success()
    }, failure)
}

function unauthorize() {
    return !takeAccessToken()
}

export { login, logout, get, post, unauthorize }