(ns miktau.nodes.events
  (:require
   [miktau.tools :as utils]
   [miktau.meta-db :as meta-db]   
   [miktau.nodes.db :as miktau-db]
   [day8.re-frame.undo :refer [undoable]]
   [re-frame.core :as refe]))

(defn init
  "TODO: TEST
   params [_ nodes-selected-set cloud-selected-set] are *nullable*"
  [_ [_ nodes-selected-set cloud-selected-set]]
  {:db
   (assoc miktau-db/default-db
          :meta (meta-db/set-loading-db (meta-db/set-page meta-db/meta-db :nodes) true)
          :cloud-selected (or cloud-selected-set #{})
          :nodes-selected (or  nodes-selected-set {}))
   :fx-redirect [:nodes/get-app-data]})
(refe/reg-event-fx :nodes/init-page (undoable "init nodes page") init)

(defn edit-nodes
  [{:keys [db]} _]
  {:db (meta-db/set-loading db true)
   :fx-redirect [:edit-nodes/init-page
                 (:nodes-selected db)
                 (:cloud-selected db)]})
(refe/reg-event-fx :nodes/edit-nodes edit-nodes)

(defn get-app-data
  "TESTED"
  [{:keys [db]} _]
  {:db (assoc db :page 1)
   :fx-redirect [:api-handler/get-app-data :nodes/got-app-data (:nodes-sorted db) #{} (:cloud-selected db) 
                 {:page 1 :page-size (or (:page-size db) 10)}]})
(refe/reg-event-fx :nodes/get-app-data get-app-data)

(defn to-page
  [{:keys [db]} [_ page]]
  {:db (assoc db :page page)
   :fx-redirect [:api-handler/get-app-data :nodes/got-app-data (:nodes-sorted db) #{} (:cloud-selected db) 
                 {:page page  :page-size (or (:page-size db) 10)}]})
(refe/reg-event-fx :nodes/to-page to-page)

(defn got-app-data
  "TESTED"
  [{:keys [db]} [_ response]]
  {:db
   (assoc
    (meta-db/set-loading db false)
    :nodes (:nodes response)
    :total-nodes (:total-nodes response)
    :total-pages (:total-nodes-pages response)
    :cloud-can-select (:cloud-can-select response)
    :cloud    (:cloud response)
    :cloud-context (:cloud-context response)
    :patriarchs         (:patriarchs response))})

(refe/reg-event-fx :nodes/got-app-data got-app-data)


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

(defn file-op
  [{:keys [db]} [_ action]]
  {:db db
   :fx-redirect [:api-handler/file-operation :nodes/get-app-data action (:nodes-selected db) (:cloud-selected db)]})
(refe/reg-event-fx :nodes/file-op file-op)

(defn redirect-to-nodes-edit
  [{:keys [db]} _]
  {:db  db
   :fx-redirect [:edit-nodes/init-page (:nodes-selected db) (:cloud-selected db)]})
(refe/reg-event-fx :nodes/redirect-to-edit-nodes redirect-to-nodes-edit)

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
  [db [_ node-id]]
  (let [nodes-selected (into #{} (or (:nodes-selected db) #{}))]
    (cond
      (not  (number? node-id)) db
      (contains? nodes-selected "*")
      (assoc db :nodes-selected #{node-id})
      (contains? nodes-selected node-id)
      (assoc db :nodes-selected (disj nodes-selected node-id))
      :else
      (assoc db :nodes-selected (conj nodes-selected node-id)))))

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
