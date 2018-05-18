(ns miktau.meta-db)

(defn meta-page? [db page-name]
  (= (get-in db [:meta :page]) page-name))

(defn set-page [meta-db page-name]
  (assoc meta-db  :page page-name))

(defn set-page-db [db page-name]
  (assoc-in db  [:meta :page] page-name))

(defn set-loading [db is-loading?]
  (assoc-in db [:meta :loading?] is-loading?))

(defn set-loading-db [db is-loading?]
  (assoc-in db [:loading?] is-loading?))


(def meta-db
  {:page :cloud
   :loading? false})
