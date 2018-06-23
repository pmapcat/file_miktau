(ns miktau.file-api.dialogs
  (:require [miktau.tools :as utils]))

(def dialog-api (aget (js/require "electron") "remote" "dialog"))

(utils/js-call
 dialog-api
 "showOpenDialog"
 nil
 (clj->js
  {:title "My awesome title"
   :buttonLabel "my awesome label"
   :filters [{:name "only documents" :extensions [".xlsx" ".docx"]}
             {:name "only word documents" :extensions [".docx"]}
             ]
   :message "Hello world on Mac OS"
   
   
   :properties
   ["openFile" "openDirectory" "multiSelections" "promptToCreate"]
}
  )
 
 )


(dialog-api)






