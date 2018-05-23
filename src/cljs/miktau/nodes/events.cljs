(ns miktau.nodes.events
  (:require
   [miktau.tools :as utils]
   [miktau.meta-db :as meta-db]   
   [miktau.nodes.db :as miktau-db]
   [re-frame.core :as refe]))

(defn init
  "TODO: TEST
   params [_ nodes-selected-set cloud-selected-set calendar-selected-dict] are *nullable*"
  [_ [_ nodes-selected-set cloud-selected-set calendar-selected-dict]]
  {:db
   (assoc miktau-db/default-db
          :meta (meta-db/set-loading-db (meta-db/set-page meta-db/meta-db :nodes) true)
          :cloud-selected (or cloud-selected-set #{})
          :calendar-selected (or  calendar-selected-dict {})
          :nodes-selected    (or  nodes-selected-set {}))
   :fx-redirect [:nodes/get-app-data]})
(refe/reg-event-fx :nodes/init-page init)

(defn edit-nodes
  [{:keys [db]} _]
  {:db (meta-db/set-loading db true)
   :fx-redirect [:edit-nodes/init-page
                 (:nodes-selected db)
                 (:cloud-selected db)
                 (:calendar-selected db)]})
(refe/reg-event-fx :nodes/edit-nodes edit-nodes)

(defn get-app-data
  "TESTED"
  [{:keys [db]} _]
  {:db db
   :fx-redirect [:api-handler/get-app-data :nodes/got-app-data (:nodes-sorted db) (:nodes-selected db) (:cloud-selected db) (:calendar-selected db)]})
(refe/reg-event-fx :nodes/get-app-data get-app-data)

(defn got-app-data
  "TESTED"
  [{:keys [db]} [_ response]]
  {:db
   (->
    (meta-db/set-loading db false)
    (assoc :nodes (:nodes response))
    (assoc :total-nodes (:total-nodes response)))})

(refe/reg-event-fx :nodes/got-app-data got-app-data)

(defn click-on-fast-access-item
  "TESTED"
  [{:keys [db]} [_ item]]
  {:db
   (let [already-selected (:calendar-selected db)]
     (cond
       (nil? item) db
       (= already-selected item)
       (assoc db :calendar-selected {})
       :else
       (assoc db :calendar-selected item)))
   :fx-redirect [:nodes/get-app-data]})

(refe/reg-event-fx :nodes/click-on-fast-access-item  click-on-fast-access-item)

(defn discard-selection
  [db]
  (assoc db :nodes-selected #{}))

(defn clicked-many-cloud-items
  "TESTED"
  [{:keys [db]} [_ items]]
  {:db
   (cond
     (not (utils/seq-of-predicate? items keyword?)) db
     (= (:cloud-selected db) (into #{} items))
     (assoc  db :cloud-selected #{})
     :else
     (assoc db :cloud-selected (into #{} items)))
   :fx-redirect [:nodes/get-app-data]})
(refe/reg-event-fx :nodes/clicked-many-cloud-items  clicked-many-cloud-items)


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
        cloud-selected)))
   :fx-redirect [:nodes/get-app-data]})

(refe/reg-event-fx :nodes/clicked-cloud-item click-on-cloud)

(defn select-all-nodes
  "TESTED"
  [db _]
  (cond
    (contains? (:nodes-selected  db) "*")
    (assoc db :nodes-selected  #{})
    :else
    (assoc db :nodes-selected #{"*"})))
(refe/reg-event-db :nodes/select-all-nodes select-all-nodes)

(defn sort-nodes
  "TESTED"
  [{:keys [db]} [_ sort-order]]
  {:db
   (if (contains? #{"name" "-name" "modified" "-modified"} (str sort-order))
     (assoc db :nodes-sorted (str sort-order))
     (assoc  db :nodes-sorted "name"))
   :fx-redirect [:nodes/get-app-data]})
(refe/reg-event-fx :nodes/sort  sort-nodes)

(defn select-node
  "TESTED"
  [db [_ file-path]]
  (let [nodes-selected (into #{} (or (:nodes-selected db) #{}))]
    (cond
      (not  (string? file-path)) db
      (contains? nodes-selected "*")
      (assoc db :nodes-selected #{file-path})
      (contains? nodes-selected file-path)
      (assoc db :nodes-selected (disj nodes-selected file-path))
      :else
      (assoc db :nodes-selected (conj nodes-selected file-path)))))

(refe/reg-event-db :nodes/select-node select-node)

(defn only-select-node
  "TESTED"
  [db [_ file-path]]
  (cond
    (not  (string? file-path)) db
    (contains? (:nodes-selected db) "*")
    (assoc db :nodes-selected #{file-path})
    :else
    (update db :nodes-selected conj file-path)))
(refe/reg-event-db :nodes/select-only-select-node only-select-node)

(defn ^:export call_select_only_select_node [fpath]
  (refe/dispatch [:nodes/select-only-select-node fpath]))
