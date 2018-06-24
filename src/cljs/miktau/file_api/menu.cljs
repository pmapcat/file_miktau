(ns miktau.file-api.menu
  (:require [miktau.tools :as utils]))

(defn set-menu!
  [menu-template]
  (let [menu (aget (js/require "electron") "remote" "Menu")]
    (utils/js-call
     menu
     "setApplicationMenu"
     (utils/js-call
      menu "buildFromTemplate"
      menu-template))))

"File"
  * "Choose root folder Ctrl+N"
  * "Import files from another folder"
  * "Open"
"Operations"
  * "Previous page Ctrl+Z"
  * "---------------"
  * "Switch to nodes  Ctrl+E"
  * "Switch to cloud  Ctrl+B"
  * "---------------"
  * "Reload app (in case of a problem) <f5>"
"About"
  * "TOS"
  * "Components"

(defn build-application-menu
  []
  (set-menu!
   [{:label "Menu"
     :submenu [{:label "Hello world"}
               {:type "separator"}
               {:label "Agregoria" :click #(.log js/console "cliekced: Agregoria")
                :accelerator "CmdOrCtrl+Shift+C"}
               {:label "Exit"}]}]))
(comment
  (build-application-menu))
