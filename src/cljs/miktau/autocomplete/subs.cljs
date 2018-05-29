(ns miktau.autocomplete.subs
  (:require [re-frame.core :as refe]
            [miktau.meta-db :refer [meta-page?]]))

(defn extract-chains
  [parent-names item]
  (let [cur-name (keyword (:name item))
        pa (conj parent-names cur-name)]
    [{:items pa
      :tag cur-name}
     (for [i (vals (:children item))]
       (extract-chains pa i))]))

(defn cloud-with-context [db _]
  (if-not (or (meta-page? db :cloud) (meta-page? db :nodes))
    {}
    (if (:tree-tag db)
      (into
       {}
       (for [[k v] (group-by :tag (flatten (extract-chains #{} (:tree-tag db))))]
         [k  (disj (:items (first v)) :root)])))))
(refe/reg-sub :autocomplete/cloud-with-context cloud-with-context)




