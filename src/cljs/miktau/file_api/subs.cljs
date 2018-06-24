(ns miktau.file-api.subs
  (:require [re-frame.core :as refe]))

(refe/reg-sub
 :file-api/main-menu
 (fn [db _]
   [{:label "File"
     :submenu
     [{:label "Choose root folder" :accelerator "Ctrl+N"
       :on-click #(refe/dispatch [:file-api/swap-root])}
      {:label "Add files"  :accelerator "Ctrl+A"
       :on-click #(refe/dispatch [:file-api/add-new-files])}
      {:role "quit"}]}
    {:label "Operations"
     :submenu
     [{:label "Undo" :accelerator "Ctrl+Z"
       :on-click #(refe/dispatch [:undo])}
      {:type "separator"}
      {:label "Switch to nodes view"  :accelerator "Ctrl+O"
       :on-click #(refe/dispatch [:nodes/init-page #{} #{}])}
      {:label "Switch to cloud view"  :accelerator "Ctrl+C"
       :on-click #(refe/dispatch [:cloud/init-page #{}])}
      {:label "Switch to edit view"  :accelerator "Ctrl+E"
       :on-click #(refe/dispatch [:cloud/init-page #{}])}
      {:type "separator"}
      {:role "reload"}]}
    {:label "About"
     :submenu
     [{:label "Components"
       :on-click #(refe/dispatch [:static/oss-components])}
      {:label "Terms Of Service"
       :on-click #(refe/dispatch [:static/tos])}
      {:label "License"
       :on-click #(refe/dispatch [:static/license])}]}]))
