(ns miktau.file-api.dialogs
  (:require [miktau.tools :as utils]))

(def dialog-api (aget (js/require "electron") "remote" "dialog"))

(defn get-current-window
  []
  (utils/js-call
   (aget (js/require "electron") "remote")
   "getCurrentWindow"))

(defn choose-root-dialog
  [cb]
  (utils/js-call
   dialog-api
   "showOpenDialog"
   (get-current-window)
   (clj->js
    {:title "Choose root directory"
     :buttonLabel "Choose root directory"
     :message "Choose root directory"
     :properties
     ["openDirectory" "showHiddenFiles"]})
   (fn [resulting-data]
     (cb (str (first (js->clj resulting-data)))))))
(comment
  (choose-root-dialog identity)
  )

(defn select-files-dialog
  [cb]
  (utils/js-call
   dialog-api
   "showOpenDialog"
   (get-current-window)
   (clj->js
    {:title "Add files to the system"
     :buttonLabel "Choose files"
     :message "Add files to the system"
     :properties
     ["openFile"
      "multiSelections"]})
   (fn [resulting-data]
     (cb (map str (js->clj resulting-data))))))
