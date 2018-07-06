(ns miktau.file-api.events
  (:require [re-frame.core :as refe]
            [miktau.meta-db :as meta-db]))

;; send them to the server, redirect result to nodes, make them selected
;; what problems arise?
;; * No chosen root directory
;; * Files are not members of a given root directory
;; * Various I/O problems, namely:
;;   * root is not writable
;;   * root does not exist (no disk)
;;   * root is full
;;   * size of given file exceeds root size
;;   * directory structure is important to user
;;   * moving operation is costly

(refe/reg-event-fx
 :file-api/swap-root
 (fn [{:keys  [db]} _]
   {:db  db
    :file-api/trigger-choose-root! [:api-handler/swap-root-directory]}))



(refe/reg-event-fx
 :file-api/add-new-files
 (fn [{:keys  [db]} _]
   {:db  db
    :file-api/trigger-choose-many-files! [:api-handler/push-new-files]}))
