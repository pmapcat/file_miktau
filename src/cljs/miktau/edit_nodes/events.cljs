(ns miktau.edit-nodes.events
  (:require
   [miktau.utils :as utils]
   [miktau.edit-nodes.query-building :as query-building]
   [miktau.edit-nodes.db :as miktau-db]
   [miktau.meta-db :as meta-db]
   [clojure.set :as clojure-set]
   [re-frame.core :as refe]))

(defn init
  "TODO: TEST
   params [_ nodes-selected-set cloud-selected-set calendar-selected-dict] are *nullable*"
  [{:keys [db]} [_ nodes-selected-set cloud-selected-set calendar-selected-dict]]
  {:db
   (assoc miktau-db/default-db
          (or (:meta db) (meta-db/set-page meta-db/meta-db :edit-nodes))
          :cloud-selected (or cloud-selected-set #{})
          :calendar-selected (or  calendar-selected-dict {})
          :nodes-selected    (or  nodes-selected-set {}))
   :fx-redirect [:edit-nodes/get-app-data]})
(refe/reg-event-fx :edit-nodes/init-page init)

(defn get-app-data
  "TESTED"
  [db]
  (if-let [core-query (query-building/build-core-query-for-action db nil)]
    {:db (meta-db/set-loading db true)
     :http-xhrio
     (utils/server-call
      {:url "/api/get-app-data"
       :params  core-query}
      :edit-nodes/got-app-data :http-error)}
    {:db (meta-db/set-loading db false)
     :fx-redirect [:http-error]}))


(refe/reg-event-fx :edit-nodes/get-app-data get-app-data)


(defn got-app-data
  "TESTED"
  [db [_ response]]
  (let [got-app-data-if-diff
        (fn [db key]
          (if-not (= (key db) (key response))
            (assoc db key  (key response))
            db))]
    (got-app-data-if-diff db :cloud-can-select)
    (got-app-data-if-diff db :nodes)))

(refe/reg-event-fx :edit-nodes/got-app-data got-app-data)

(defn file-operation-fx
  "TESTED"
  [{:keys [db]} [_ operation-name]]
  (if-let [api-call (query-building/build-bulk-operate-on-files db operation-name nil)]
    ;; TODO: wht is :mutable-server-operation ?
    {:http-xhrio (utils/server-call api-call :mutable-server-operation :http-error)
     :db db}
    {:db db}))
(refe/reg-event-fx :edit-nodes/file-operation file-operation-fx)

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
  (if-let [api-call (query-building/build-update-records db nil)]
    {:db  (assoc  db :cloud-selected (:cloud-selected (build-updated-drilldown-on-nodes-or-cloud db))
                  :nodes-temp-tags-to-add ""
                  :nodes-temp-tags-to-delete #{})
     :http-xhrio (utils/server-call api-call :edit-nodes/redirect-to-nodes :http-error)}
    {:db db}))

(refe/reg-event-fx :edit-nodes/submit-tagging submit-tagging)

(defn cancel-tagging
  "TESTED"
  [db _]
  {:db
   (assoc
    db
    :nodes-temp-tags-to-add ""
    :nodes-temp-tags-to-delete #{})
   :fx-redirect  [:edit-nodes/redirect-to-nodes]})

(refe/reg-event-db :edit-nodes/cancel-tagging cancel-tagging)

;; TODO implement
(refe/reg-event-fx
 :edit-nodes/redirect-to-nodes
 (fn [{:keys [db]} _]
   {:db db}))



