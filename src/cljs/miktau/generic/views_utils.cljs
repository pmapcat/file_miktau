(ns miktau.generic.views-utils
  (:require [clojure.string :as cstring]))


(defn style-convert
  [style]
  (println
   (str
    (into {}
          (for [i  (cstring/split style #";")]
            (let [[left right] (cstring/split i ":")]
              [(keyword left) (str right)]))))))

(defn icon [kind]
  [:i.material-icons {:style {:font-size "0.8em"}} kind])

(defn icon-rotated [degree kind]
  [:i.material-icons {:style {:font-size "0.8em"}
                      :class (str "mik-rotate-" degree)} kind])


(defn position-absolute [style body]
  [:div {:style {:position "relative"}}
   [:div {:style (assoc style :position "absolute")}
    body]])
