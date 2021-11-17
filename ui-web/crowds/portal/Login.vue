<template>
  <div style='width: 100%;height: 100%;' class="login-container">
    <k-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" autocomplete="on" label-position="left">

      <div class="title-container">
        <h3 class="title">Crowd</h3>
      </div>

      <k-form-item prop="username" label="用户名：" style="width: 100%;color: white;font-size: 17px;">
        <k-input
                ref="username"
                v-model="loginForm.username"
                placeholder="Username"
                name="username"
                type="text"
                tabindex="1"
                autocomplete="on"
                style="width: 100%;"
        />
      </k-form-item>

      <k-form-item prop="password" label="密   码：" style="width: 100%;color: white;font-size: 17px;">
        <k-input
                :key="passwordType"
                ref="password"
                v-model="loginForm.password"
                :type="passwordType"
                placeholder="Password"
                name="password"
                tabindex="2"
                autocomplete="on"
                @blur="capsTooltip = false"
                @keyup.enter.native="handleLogin"
                style="width: 100%;"
        />
        <span class="show-pwd" @click="showPwd">
        <!--<svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />-->
      </span>
      </k-form-item>

      <k-button :loading="loading" type="primary" style="width:100%;margin-top:20px;" @click="handleLogin">登&nbsp;&nbsp;&nbsp;&nbsp;录</k-button>

    </k-form>
  </div>
</template>

<script>
  import Main from './Main'
  import {subscribeStates,commitState} from '../../commons/Crowding'
  import {updateLoginInfoAction} from '../../reducers/connectInfo'

  export default {
    name: "login",
    components: {
      'Main':Main
    },
    data() {
      const validateUsername = (rule, value, callback) => {
        if (!value) {
          callback(new Error('Please enter the correct user name'))
        } else {
          callback()
        }
      }
      const validatePassword = (rule, value, callback) => {
        if (value.length < 6) {
          callback(new Error('The password can not be less than 6 digits'))
        } else {
          callback()
        }
      }
      return {
        connectState: -1,
        loginForm: {
          username: '',
          password: ''
        },
        loginRules: {
          username: [{ required: true, trigger: 'blur', validator: validateUsername }],
          password: [{ required: true, trigger: 'blur', validator: validatePassword }]
        },
        passwordType: 'password',
        capsTooltip: false,
        loading: false,
        showDialog: false,
        redirect: undefined,
        otherQuery: {}
      }
    },
    watch: {
      $route: {
        handler: function(route) {
          // const query = route.query
          // if (query) {
          //   this.redirect = query.redirect
          //   this.otherQuery = this.getOtherQuery(query)
          // }
        },
        immediate: true
      }
    },
    created() {
      // window.addEventListener('storage', this.afterQRScan)
      //this.unregisterStateListener = subscribeStates(['connectInfo'], this.connectInfoListener); //XXX：页面销毁时注销
      this.$subscribeStates(['connectInfo'], this.statesChanged)
    },
    mounted() {
      if (this.loginForm.username === '') {
        this.$refs.username.focus()
      } else if (this.loginForm.password === '') {
        this.$refs.password.focus()
      }
    },
    methods: {
      statesChanged (connectInfo) {
        this.connectState = connectInfo.lastMessageTime;
      },
      checkCapslock ({shiftKey, key} = {}) {
        if (key && key.length === 1) {
          if (shiftKey && (key >= 'a' && key <= 'z') || !shiftKey && (key >= 'A' && key <= 'Z')) {
            this.capsTooltip = true
          } else {
            this.capsTooltip = false
          }
        }
        if (key === 'CapsLock' && this.capsTooltip === true) {
          this.capsTooltip = false
        }
      },
      showPwd () {
        if (this.passwordType === 'password') {
          this.passwordType = ''
        } else {
          this.passwordType = 'password'
        }
        this.$nextTick(() => {
          this.$refs.password.focus()
        })
      },
      handleLogin () {

        let params = {
          "userName": this.loginForm.username,
          "deviceCode" : 'xxxx',
          "deviceName" : 'xxxxxxx',
          "version" : '',
          "phoneNumber": this.loginForm.username,
          "deviceId" : 'xxxxxxxxxxx',
          "password" : this.loginForm.password
        }
        this.$invoke("/portal/login", params, (error, data) => {
          if(error) {
            alert(error.message)
          } else {
            this.loading = false;
            commitState(updateLoginInfoAction(data));
            //AsyncStorage.setItem('userName', this.phoneNumber);
          }
        });

      }
    }
  }
</script>

<style lang="scss">
  $bg:#2d3a4b;
  $dark_gray:#889aa4;
  $light_gray:#eee;

  .login-container {
    min-height: 100%;
    width: 100%;
    background-color: $bg;
    overflow: hidden;

    .login-form {
      position: relative;
      width: 520px;
      max-width: 100%;
      padding: 160px 35px 0;
      margin: 0 auto;
      overflow: hidden;
    }

    .tips {
      font-size: 14px;
      color: #fff;
      margin-bottom: 10px;

      span {
        &:first-of-type {
          margin-right: 16px;
        }
      }
    }

    .svg-container {
      padding: 6px 5px 6px 15px;
      color: $dark_gray;
      vertical-align: middle;
      width: 30px;
      display: inline-block;
    }

    .title-container {
      position: relative;

      .title {
        font-size: 26px;
        color: $light_gray;
        margin: 0px auto 40px auto;
        text-align: center;
        font-weight: bold;
      }
    }

    .show-pwd {
      position: absolute;
      right: 10px;
      top: 7px;
      font-size: 16px;
      color: $dark_gray;
      cursor: pointer;
      user-select: none;
    }

    .thirdparty-button {
      position: absolute;
      right: 0;
      bottom: 6px;
    }

    @media only screen and (max-width: 470px) {
      .thirdparty-button {
        display: none;
      }
    }
  }
</style>
