/* eslint-disable no-process-exit */
const childProcess = require('child_process')
const chalk = require('chalk')
const inquirer = require('inquirer')
const path = require('path')

const rootDir = path.resolve(__dirname, '../')

const devConfig = require('../config/devConfig')

const {udid} = devConfig

start()

async function start() {
  let deviceUdid

  // config in the devConfig file
  if (udid) {
    deviceUdid = udid
  } else {
    const result = childProcess.execSync('instruments -s devices').toString()
    // udid, length 40, like '93ab5ffa6efe55b21d59385bcc782cabcdea5414'
    const regSrc = '\\n.+\\(.+\\)\\s\\[([a-z]|[0-9]){40}\\]'
    const matchAry = result.match(new RegExp(regSrc, 'g'))

    if (matchAry) {
      const {length} = matchAry
      if (length === 1) {
        deviceUdid = getUdid(matchAry[0])
      } else {
        const question = [{
          type: 'list',
          name: 'udid',
          message: 'Which device to start app development?',
          choices: matchAry,
          filter(val) {
            return getUdid(val)
          }
        }]
        const answer = await inquirer.prompt(question)
        deviceUdid = answer.udid
      }
    } else {
      const warnStr = 'No device detected! Please connect your device through USB '
        + 'and trust this computer'
      console.log(chalk.blue(warnStr))

      const question = [
        {
          type: 'confirm',
          name: 'startSimulator',
          message: 'Do you want to open app in Simulator?',
          default: false
        }
      ]
      const {startSimulator} = await inquirer.prompt(question)
      if (startSimulator) {
        const cmd = `react-native run-ios`
        childProcess.execSync(cmd, {
          stdio: 'inherit',
          cwd: process.cwd()
        })
      }
      return
    }
  }
  const targetAry = getAllTarget().sort((ele) => {
    // LK_dev is default dev scheme
    return !ele.endsWith('dev')
  })
  const questionAry = [
    {
      type: 'list',
      name: 'target',
      message: 'Which target do you want to start?',
      choices: targetAry
    }
  ]
  const answer = await inquirer.prompt(questionAry)
  console.log('starting....')
  const cmd = getCmd(deviceUdid, answer.target)

  console.log(chalk.green(cmd))
  timeCount(() => {
    childProcess.execSync(cmd, {
      stdio: 'inherit'
    })
  })
  console.log('cmd executed successfully')
}

function getCmd(udidParam, target) {
  return `
react-native run-ios --udid ${udidParam} --scheme ${target}
`
}

function getUdid(str) {
  const ary = str.match(/\[(([a-z]|[0-9]){40})]/)

  return ary[1]
}

async function timeCount (func) {
  const shouldFormat = true
  const shouldLog = true
  const start = Date.now()
  func()

  let result = Date.now() - start
  if (shouldFormat) {
    const ms = result % 1000
    const sCount = Math.floor(result / 1000)
    const s = sCount % 60
    const minCount = Math.floor(sCount / 60)
    let minStr = ''
    let sStr = ''
    if (minCount) {
      minStr = `${minCount}min-`
    }
    if (s) {
      sStr = `${s}s-`
    }
    result = `${minStr}${sStr}${ms}ms`
  }
  if (shouldLog) {
    console.log(`time elapsed: ${result}`)
  }
  return result
}

function getAllTarget() {
  let result = []
  const iosFolder = path.resolve(rootDir, 'ios')
  const output = childProcess.execSync(`xcodebuild -list`, {
    cwd: iosFolder
  }).toString()
  const regex = /Targets:((?:.|\n)*)Build Configurations:/
  const matchedAry = output.match(regex)
  if (matchedAry) {
    const captured = matchedAry[1]
    result = captured.split('\n').map(ele => ele.trim()).filter(ele => ele)
  }
  return result
}
