(ns miktau.generic.subs
  (:require [re-frame.core :as refe]
            [miktau.meta-db :as meta-db]))

(refe/reg-sub
 :generic/test-db
 (fn  [db _] db))


(defn meta-items [db _]
  (or (:meta db)
      meta-db/meta-db))
(refe/reg-sub :meta meta-items)
