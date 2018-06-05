(ns miktau.cloud.events
  (:require
   [miktau.tools :as utils]
   [miktau.cloud.db :as miktau-db]
   [miktau.meta-db :as meta-db]
   [day8.re-frame.undo :refer [undoable]]
   [re-frame.core :as refe]))

(defn init
  "TODO: TEST"
  [_ [_ cloud-selected-set]]
  {:db
   (assoc miktau-db/default-db
          :meta
          (meta-db/set-loading-db (meta-db/set-page meta-db/meta-db :cloud) true)
          :cloud-selected (or cloud-selected-set #{}))
   :fx-redirect [:cloud/get-app-data]})
(refe/reg-event-fx :cloud/init-page (undoable "init page") init)

(defn redirect-to-nodes
  [{:keys [db]} [_ all-nodes-selected?]]
  {:db  db
   :fx-redirect [:nodes/init-page (if all-nodes-selected? #{"*"} #{}) (:cloud-selected db)]})
(refe/reg-event-fx :cloud/redirect-to-nodes
                   redirect-to-nodes)

(defn redirect-to-nodes-edit
  [{:keys [db]} [_ all-nodes-selected?]]
  {:db  db
   :fx-redirect [:edit-nodes/init-page (if all-nodes-selected? #{"*"} #{}) (:cloud-selected db)]})
(refe/reg-event-fx :cloud/redirect-to-edit-nodes
                   redirect-to-nodes-edit)

(defn file-op
  [{:keys [db]} [_ action]]
  {:db (meta-db/set-loading db true)
   :fx-redirect [:api-handler/file-operation :cloud/get-app-data action #{"*"} (:cloud-selected db)]})
(refe/reg-event-fx :cloud/file-op file-op)

(defn filtering
  "TESTED"
  [db [_ data]]
  (assoc db :filtering
         (utils/allowed-tag-or-include-empty? (str data) (db :filtering))
         (str data)))
(refe/reg-event-db :cloud/filtering filtering)

(defn get-app-data
  "TESTED"
  [{:keys [db]} _]
  {:db db
   :fx-redirect [:api-handler/get-app-data :cloud/got-app-data "" #{} (:cloud-selected db)
                 {:page-size 1}]})
(refe/reg-event-fx :cloud/get-app-data get-app-data)

(defn got-app-data
  "TESTED"
  [db [_ response]]
  (->
   (meta-db/set-loading db false)
   (assoc :cloud (:cloud response))
   (assoc :total-nodes (:total-nodes response))
   (assoc :cloud-can-select (:cloud-can-select response))
   (assoc :patriarchs (:patriarchs response))
   (assoc :cloud-context (:cloud-context response))))
(refe/reg-event-db :cloud/got-app-data got-app-data)


(defn discard-selection
  [db]
  (assoc db :cloud-selected #{}))
(refe/reg-event-db :cloud/discard-selection discard-selection)

(defn click-on-cloud
  "TESTED"
  [{:keys [db]} [_ item]]
  {:db
   (assoc
    db
    :cloud-selected
    (let [cloud-selected (into #{} (:cloud-selected db #{}))]
      (cond
        (and  (keyword? item) (contains? cloud-selected item))
        (disj cloud-selected item)
        (keyword? item)
        (conj cloud-selected item)
        :else
        cloud-selected))
    :cloud-can-select {})
   :fx-redirect [:cloud/get-app-data]})
(refe/reg-event-fx :cloud/clicked-cloud-item click-on-cloud)


(defn clicked-many-cloud-items
  "TESTED"
  [{:keys [db]} [_ items]]
  {:db
   (cond
     (not (utils/seq-of-predicate? items keyword?)) db
     :else
     (assoc db :cloud-selected (into #{} items)))
   :fx-redirect [:cloud/get-app-data]})
(refe/reg-event-fx :cloud/clicked-many-cloud-items  clicked-many-cloud-items)



(defn click-on-disabled-cloud
  "TESTED"
  [{:keys [db]} [_ item]]
  {:db
   (if (keyword? item)
     (assoc  (discard-selection db)
             :cloud-selected #{item}
             :cloud-can-select {})
     db)
   :fx-redirect [:cloud/get-app-data]})

(refe/reg-event-fx :cloud/clicked-disabled-cloud-item click-on-disabled-cloud)
