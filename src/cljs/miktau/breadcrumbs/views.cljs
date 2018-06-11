(ns miktau.breadcrumbs.views
  (:require [re-frame.core :as refe]))

(defn for-every-and-last
  [data-set]
  (let [last-by-index (dec (count data-set))]
    (for [[v k] (map list (range) data-set)]
      [k {:point k
          :index v
          :last? (= v last-by-index)
          :first? (= v 0)}])))
(defn inject-event
  "Injects first param into event vector
   have no idea how to insert at a certain index 
   in Clojure vector, so, doing it crude way"
  [param event]
  (into [] (cons (first event) (cons param (rest event)))))

(defn breadcrumbs [redirector]
  (let [breadcrumbs @(refe/subscribe [:breadcrumbs/breadcrumbs])]
    [:div
     [:div.mik-flush-right.unstyled-link {:style {:padding-right "5px" }}
      "["
      [:a.blue-clickable.unstyled-link {:href "#" :on-click #(refe/dispatch  [:nodes/init-page #{}  #{} {}]) } "Nodes "]
      [:a.blue-clickable.unstyled-link {:href "#" :on-click #(refe/dispatch  [:cloud/init-page #{}  {}]) } "Cloud"]
      "]"]

     (if (:should-show-clear? breadcrumbs)
       [:a.unstyled-link.red-clickable {:on-click #(refe/dispatch [:breadcrumbs/clear redirector]) :style {:padding-right "5px"}} "[Clear]"]
       [:span])

     ;; cloud items
     [:span {:style {:padding-right "5px"}}
      
      (for [[item meta-item] (for-every-and-last (:cloud-items breadcrumbs))]
        ^{:key (:name item)}
        [:span [:a.unstyled-link.black-clickable {:on-click #(refe/dispatch (inject-event redirector (:on-click item)))} (:name item)]
         (if-not (:last? meta-item) " > " " ")])]
     
     ;; potential selection
     (if-not (empty? (:cloud-can-select breadcrumbs))
       [:span
        "( "
        (for [[item meta-item]  (for-every-and-last
                                 (if (:show-all? breadcrumbs)
                                   (:cloud-can-select breadcrumbs)
                                   (take 10 (:cloud-can-select breadcrumbs))))]
          ^{:key (:name item)}
          [:span
           [:a.unstyled-link.green-clickable {:on-click #(refe/dispatch (inject-event redirector (:on-click item)))} (:name item)]
           (if-not (:last? meta-item) " • " " ")])
        (if (:can-expand? breadcrumbs)
          [:a.unstyled-link.green-clickable {:style {:padding-left "5px" } :on-click #(refe/dispatch [:breadcrumbs/breadcrumbs-show-all?-switch])} "…" ]
          [:span]) " )"]
       [:span])]))
