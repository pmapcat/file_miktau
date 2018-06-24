(ns miktau.file-api.effects
  (:require [re-frame.core :as refe]
            [miktau.file-api.dialogs :as dialogs]
            [miktau.tools :as utils]))

(refe/reg-fx
 :file-api/trigger-choose-root!
 (fn [callback]
   (dialogs/choose-root-dialog
    (fn [new-root-dir]
      (if-not (empty? new-root-dir)
        (refe/dispatch (utils/inject-event new-root-dir callback))
        identity)))))


(refe/reg-fx
 :file-api/trigger-choose-many-files!
 (fn [callback]
   (dialogs/select-files-dialog
    (fn [new-many-root-dirs]
      (if-not (empty? new-many-root-dirs)
        (refe/dispatch (utils/inject-event new-many-root-dirs callback))
        identity)))))









