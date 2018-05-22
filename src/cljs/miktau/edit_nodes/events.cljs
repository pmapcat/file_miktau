(ns miktau.edit-nodes.events
  (:require
   [miktau.tools :as utils]
   [miktau.edit-nodes.query-building :as query-building]
   [miktau.edit-nodes.db :as miktau-db]
   [miktau.meta-db :as meta-db]
   [clojure.set :as clojure-set]
   [re-frame.core :as refe]))

(defn init
  "TODO: TEST
   params [_ nodes-selected-set cloud-selected-set calendar-selected-dict] are *nullable*"
  [_ [_ nodes-selected-set cloud-selected-set calendar-selected-dict]]
  {:db
   (assoc miktau-db/default-db
          :meta
          (meta-db/set-loading-db (meta-db/set-page meta-db/meta-db :edit-nodes) true)
          :cloud-selected (or cloud-selected-set #{})
          :calendar-selected (or  calendar-selected-dict {})
          :nodes-selected    (or  nodes-selected-set {}))
   :fx-redirect [:edit-nodes/get-app-data]})
(refe/reg-event-fx :edit-nodes/init-page init)

(defn get-app-data
  "TESTED"
  [{:keys [db]} _]
  {:db db
   :fx-redirect [:api-handler/get-app-data :edit-nodes/got-app-data "" (:nodes-selected db) (:cloud-selected db) (:calendar-selected db)]})
(refe/reg-event-fx :edit-nodes/get-app-data get-app-data)

(defn got-app-data
  "TESTED"
  [db [_ response]]
  (->
   (meta-db/set-loading db false)
   (assoc :cloud-can-select (:cloud-can-select response))
   (assoc :nodes (:nodes response))))
(refe/reg-event-db :edit-nodes/got-app-data got-app-data)

(defn file-operation
  [{:keys [db]} [_ operation-name]]
  {:db db
   :fx-redirect [:api-handler/file-operation :edit-nodes/get-app-data operation-name (:nodes-selected db) (:cloud-selected db) (:calendar-selected db)]})
(refe/reg-event-fx :generic/file-operation file-operation)

(defn delete-tag-from-selection
  "TESTED"
  [db [_ tag-item]]
  (let [node-temp-tags (into #{} (or (:nodes-temp-tags-to-delete db) #{}))]
    (assoc
     db
     :nodes-temp-tags-to-delete
     (cond
       (and (keyword? tag-item) (contains? node-temp-tags tag-item))
       (disj node-temp-tags tag-item)
       (keyword? tag-item)
       (conj node-temp-tags tag-item)
       :else
       node-temp-tags))))
(refe/reg-event-db :edit-nodes/delete-tag-from-selection delete-tag-from-selection)

(defn add-tag-to-selection
  "TESTED"
  [db [_ tag-item]]
  (if (string? tag-item) (assoc db :nodes-temp-tags-to-add tag-item) db))
(refe/reg-event-db :edit-nodes/add-tags-to-selection add-tag-to-selection)

(defn build-updated-drilldown-on-nodes-or-cloud
  "TESTED"
  [db]
  (->>
   (clojure-set/difference
    (:cloud-selected db)
    (:nodes-temp-tags-to-delete db))
   (clojure-set/union
    (into #{} (map keyword (utils/find-all-tags-in-string (:nodes-temp-tags-to-add db)))))
   (assoc db :cloud-selected)))

(defn submit-tagging
  "TESTED"
  [{:keys [db]} _]
  {:db  (assoc  db :cloud-selected (:cloud-selected (build-updated-drilldown-on-nodes-or-cloud db))
                :nodes-temp-tags-to-add ""
                :nodes-temp-tags-to-delete #{})
   :fx-redirect
   [:api-handler/build-update-records :edit-nodes/get-app-data (:nodes-temp-tags-to-add db) (:nodes-temp-tags-to-delete db)
    (:nodes-selected db) (:cloud-selected db) (:calendar-selected db)]})
(refe/reg-event-fx :edit-nodes/submit-tagging submit-tagging)

(defn cancel-tagging
  "TESTED"
  [db _]
  {:db (assoc db :nodes-temp-tags-to-add "" :nodes-temp-tags-to-delete #{})
   :fx-redirect  [:edit-nodes/redirect-to-nodes]})
(refe/reg-event-fx :edit-nodes/cancel-tagging cancel-tagging)


;; TODO implement
(refe/reg-event-fx
 :edit-nodes/redirect-to-nodes
 (fn [{:keys [db]} _]
   {:db db}))
