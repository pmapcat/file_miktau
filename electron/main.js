const electron = require('electron')
const path = require('path')
const child_process = require('child_process')

const app = electron.app
const BrowserWindow = electron.BrowserWindow


/*************************************************************
 * auto updating code
 *************************************************************/
const { autoUpdater } = require("electron-updater")
const log = require("electron-log")

log.transports.file.level = "debug"
autoUpdater.logger = log
autoUpdater.checkForUpdatesAndNotify()

/*************************************************************
 * go subprocess handling
 *************************************************************/
const SERVER_PORT = 4242
const BACKEND_FOLDER = 'backend'
const ISDEV = false // (process.env.ELECTRON_ENV !== "production")
const CALLABLE = 'backend'
const CALLABLE_WIN = 'backend.exe'
const APP_URL = (function() {
  if (ISDEV) {
    log.debug("this is dev env, running on localhost")
    return "http://localhost"
  }
  return "http://127.0.0.1:" + SERVER_PORT
})()

let server_running = null

const getServerPath = function() {
  if (process.platform === 'win32') {
    return path.join(__dirname, BACKEND_FOLDER, CALLABLE_WIN);
  }
  return path.join(__dirname, BACKEND_FOLDER, CALLABLE);
}

const selectPort = function() {
  return SERVER_PORT
}

const createServer = function() {
  let executable = getServerPath()
  let port = selectPort()
  server_running = child_process.execFile(executable, ['--port', port], function(error, stdout, stderr) {
    log.debug(stderr);
    log.error(error)
    log.debug(stdout)
  });
  if (server_running != null) {
    log.debug('server is running on port ' + port)
  }
}

const exitServer = function() {
  server_running.kill()
  server_running = null
}

app.on('ready', createServer)
app.on('will-quit', exitServer)

/*************************************************************
 * window management
 *************************************************************/

let mainWindow = null

const createWindow = () => {
  mainWindow = new BrowserWindow({ width: 1000, height: 600, resizable: false })


  mainWindow.loadURL(APP_URL, { "extraHeaders": "pragma: no-cache\n" })
  mainWindow.webContents.openDevTools()

  mainWindow.on('closed', () => {
    mainWindow = null
  })
}

app.on('ready', createWindow)

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

app.on('activate', () => {
  if (mainWindow === null) {
    createWindow()
  }
})
