class Stopwatch {
    constructor() {
        this.reset();
    }

    reset() {
        let result = this.date;
        this.date = new Date();
        return this.date;
    }

    elapseTime() {
        return new Date() - this.date;
    }
}

Date.prototype.format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

const app = new Vue({
    el: '#app',
    data: {
        path: 'ws://127.0.0.1:8080/websocket',
        socket: null,
        message: [],
        sendName: '',
        receiveName: '',
        text: '',
        userCount: [],
        searchVal: '',
        url: '',
        userListOpen: true,
        time: new Stopwatch()
    },
    mounted() {
        this.sendName = $.cookie("sendName")
        this.url = '/upload/' + this.sendName + ".jpg"
        this.init()
    },
    methods: {
        init() {
            if (typeof (WebSocket) === "undefined") {
                alert("您的浏览器不支持socket")
            } else {
                // 实例化socket
                this.socket = new WebSocket(this.path)
                // 监听socket连接
                this.socket.onopen = this.open
                // 监听socket错误信息
                this.socket.onerror = this.error
                // 监听socket消息
                this.socket.onmessage = this.getMessage
            }

        },
        open() {
            console.log("socket连接成功")
            if (this.sendName != "") {
                this.socket.send(JSON.stringify({sendName: this.sendName, type: "setting"}))
            }
        },
        error() {
            console.log("连接错误")
        },
        getMessage(msg) {
            let parse = JSON.parse(msg.data);
            if (parse.type == "userCount") {
                this.userCount = parse.userCount
                return
            } else if(parse.receiveName == "机器人"){
                this.message.push(parse);
            }else if (parse.sendName == this.receiveName && parse.receiveName != "在线群聊" && parse.sendName != this.sendName) {
                this.message.push(parse)
            } else if (parse.receiveName == "在线群聊" && this.receiveName == parse.receiveName) {
                this.message.push(parse)
                let messageList = [];
                let chatData = store.get("在线群聊")
                if (chatData != null) {
                    messageList = chatData.message;
                    messageList.push(parse)
                }
                store.set("在线群聊", {message: messageList});
                this.$nextTick(function (){
                    let elementById = document.getElementById("scrollIV");
                    elementById.scrollTop = elementById.scrollHeight;
                })
                return;
            } else if (parse.receiveName == "在线群聊" && this.receiveName != parse.receiveName) {
                let chatData = store.get("在线群聊")
                let messageList = [];
                if (chatData != null) {
                    messageList = chatData.message;
                    messageList.push(parse)
                }
                store.set("在线群聊", {message: messageList});
                this.nextTick()
                return;
            }
            this.$nextTick(function (){
                let elementById = document.getElementById("scrollIV");
                elementById.scrollTop = elementById.scrollHeight;
            })
            let sendName = parse.sendName;
            if (this.sendName == sendName) return;
            let messageList = [];
            let chat = store.get(sendName);
            if (chat != null) {
                messageList = chat.message;
                messageList.push(parse)
            }
            store.set(sendName, {message: messageList});

        },
        send() {
            if (this.sendName == "" || this.sendName == null) {
                alert("请设置昵称")
                this.sendName = prompt("昵称：");
                if (this.sendName == "在线群聊") return;
                $.cookie("sendName", this.sendName, {expires: 7, path: "/"})
                let json = JSON.stringify({sendName: this.sendName, type: "setting"});
                this.socket.send(json)
            } else if (this.text == null || this.text == "") {
                return;
            } else if (this.receiveName == "") {
                alert("请指定发送人")
            } else if (this.text.length < 0) {
                return
            } else {
                let type = this.receiveName == "在线群聊" ? "sendMessageAll" : this.receiveName == "机器人" ? "sendRobot" : "sendMessage";
                console.log(this.receiveName == "机器人")
                let createDate = this.time.elapseTime() > 5 * 60 * 1000 ? this.time.reset() : null;
                console.log(createDate);
                let json = JSON.stringify({
                    text: this.text,
                    type: type,
                    receiveName: this.receiveName,
                    sendName: this.sendName,
                    url: this.url,
                    createDate: createDate ? createDate.format("yyyy-M-d h:m:s") : null
                })
                this.socket.send(json)
                this.text = ""
                if (this.receiveName != "在线群聊") {
                    this.message.push(JSON.parse(json))
                }
                console.log(json)
                store.set(this.receiveName, {message: this.message});
                document.getElementById("send").innerHTML = "";
                this.$nextTick(function (){
                    let elementById = document.getElementById("scrollIV");
                    elementById.scrollTop = elementById.scrollHeight;
                })
            }
        },
        close() {
            console.log("socket连接关闭")
        },
        onAcquireName: function (name) {
            this.userListOpen = false;
            this.receiveName = name
            this.message = []
            let chat = store.get(this.receiveName);
            if (chat != null) {
                this.message = store.get(this.receiveName).message;
            } else {
                store.set(this.receiveName, {message: this.message})
            }
            this.$nextTick(function (){
                let elementById = document.getElementById("scrollIV");
                elementById.scrollTop = elementById.scrollHeight;
            })
        },
        chooseImg(file) {
            console.log(this.sendName);
            if (file != null && this.sendName != null) {
                document.forms['upload'].submit()
            }
        },
        edit(event) {
            this.text = event.target.innerHTML;
        }
    },
    destroyed() {
        // 销毁监听
        this.socket.onclose = this.close
    },
    computed: {
        searchList: function () {
            return this.userCount.filter((text) => {
                return text.name.toLowerCase().match(this.searchVal.toLowerCase());
            })
        }
    }
})