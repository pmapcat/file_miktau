(ns miktau.file-api.menu
  (:require [miktau.tools :as utils]
            [re-frame.core :as refe]))

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

(def menu-template
  [{:label "File"
    :submenu
    [{:label "Choose root folder" :accelerator "Ctrl+N"
      :click #(refe/dispatch [:file-api/swap-root])}
     {:label "Add files"  :accelerator "Ctrl+A"
      :click #(refe/dispatch [:file-api/add-new-files])}
     {:role "quit"}]}
   {:label "Operations"
    :submenu
    [{:label "Undo" :accelerator "Ctrl+Z"
      :click #(refe/dispatch [:undo])}
     {:type "separator"}
     {:label "Switch to nodes"  :accelerator "Ctrl+O"
      :click #(refe/dispatch [:nodes/init-page #{} #{}])}
     {:label "Switch to cloud"  :accelerator "Ctrl+L"
      :click #(refe/dispatch [:cloud/init-page #{}])}
     {:label "Switch to edit view"  :accelerator "Ctrl+D"
      :click #(refe/dispatch [:edit-nodes/init-page #{"*"} #{}])}
     
     {:type "separator"}
     {:role "reload" :label "Reload app (on errors)"}]}
   {:label "About"
    :submenu
    [{:label "Components"
      :click #(refe/dispatch [:generic/init-static-page :oss-components])}
     {:label "Terms Of Service"
      :click #(refe/dispatch [:generic/init-static-page :tos])}
     {:label "About"
      :click #(refe/dispatch [:generic/init-static-page :about])}]}])

(defn set-menu!
  []
  (let [menu (aget (js/require "electron") "remote" "Menu")]
    (utils/js-call
     menu
     "setApplicationMenu"
     (utils/js-call
      menu "buildFromTemplate"
      menu-template))))




(comment
  (build-application-menu))
