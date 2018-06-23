(ns miktau.meta-db)


(defn meta-page? [db page-name]
  (= (get-in db [:meta :page]) page-name))
(defn it-is-meta-page= [db]
  (get-in db [:meta :page]))


(defn set-page [meta-db page-name]
  (assoc meta-db  :page page-name))

(defn set-page-db [db page-name]
  (assoc-in db  [:meta :page] page-name))


(defn set-loading [db is-loading?]
  (assoc-in db [:meta :loading?] is-loading?))

(defn set-loading-db [db is-loading?]
  (assoc-in db [:loading?] is-loading?))
(defn set-error
  [db error-str]
  (assoc-in db [:meta :error] (str error-str)))
(defn clear-error
  [db]
  (set-error db nil))
(defn error? [db]
  (not (nil? (get-in db [:meta :error]))))

(defn set-arbitrary-key [db key val]
  (assoc-in db [:meta key] val))
(defn get-arbitrary-key [db key]
  (get-in db [:meta key]))

(def meta-db
  {:page :init
   :loading? false
   :error nil})
