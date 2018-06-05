(ns miktau.edit-nodes.views
  (:require [miktau.generic.views-utils :as views-utils]
            [re-frame.core :as refe]))

(defn for-every-and-last
  [data-set]
  (let [last-by-index (dec (count data-set))]
    (for [[v k] (map list (range) data-set)]
      [k {:point k
          :index v
          :last? (= v last-by-index)
          :first? (= v 0)}])))

(defn filter-input []
  [:div.pure-g
   [:div.pure-u-23-24
    [:input
     {:type "text" :placeholder "Type tag in here to add new tag" 
      :style {:width "100%" :height "1.9em"}}]]
   [:div.pure-u-1-24.mik-flush-right
    [:div.pure-button.pure-button-primary  {:style {:width "100%"}}
     [views-utils/icon "search"]]]])

(defn breadcrumbs []
  (let [breadcrumbs @(refe/subscribe [:edit-nodes/breadcrumbs])]
    [:div.mik-cut-top
     [:a.unstyled-link.red-clickable {:on-click #(refe/dispatch [:edit-nodes/clear]) :style {:padding-right "5px"}} "«Clear»"]
     [:span {:style {:padding-right "5px"}}]
     [:span.unstyled-link
      "| Influence on: "   (:total-records breadcrumbs) " |"]
     ;; tags to add
     (if-not (empty? (:tags-to-add breadcrumbs))
       [:span.unstyled-link.padded-as-button
        [:span.added-in "Tags to add: "] "  ( "
        (for [[item meta-item]  (for-every-and-last (:tags-to-add breadcrumbs))]
          ^{:key (:name item)}
          [:span
           [:a.unstyled-link.green-clickable {:on-click #(refe/dispatch (:on-click item))} (:name item)]
           (if-not (:last? meta-item) " • " " ")])
        " )"]
       [:span])
     ;; tags to delete 
     (if-not (empty? (:tags-to-delete breadcrumbs))
       [:span.unstyled-link
        [:span.crossed-out "Tags to delete: "]
        " ( " 
        (for [[item meta-item]  (for-every-and-last (:tags-to-delete breadcrumbs))]
          ^{:key (:name item)}
          [:span
           [:a.unstyled-link.green-clickable {:on-click #(refe/dispatch (:on-click item))} (:name item)]
           (if-not (:last? meta-item) " • " " ")])
        " )"]
       [:span])]))

(defn general-cloud-tag-item [tag]
  [:a.tag
   {:key  (:key-name tag)
    :on-click
    (if (:disabled? tag)
      #(refe/dispatch [:edit-nodes/add-tag-to-selection      (tag :key-name)])
      #(refe/dispatch [:edit-nodes/delete-tag-from-selection (tag :key-name)]))
    :class
    (str
     (cond (:selected? tag) "selected"
           (:can-select? tag) "can-select"
           (:disabled? tag) "disabled") " "
     (cond (:to-add? tag) "added-in"
           (:to-delete? tag) "crossed-out"))
    :style
    {:font-size
     (str  (+ 0.6 (* 2.4  (tag :weighted-size))) "em")
     :cursor "pointer"}}
   (:name tag) " "])

(defn general-cloud []
  [:div
   (for [tag  @(refe/subscribe [:edit-nodes/cloud])]
     [:span {:key (:key-name tag)}
      [general-cloud-tag-item tag]])])

(defn main []
  [:div.padded-as-button {:style {:height "100%"}}
   [:div.pure-u-1.padded-as-button {:style {:box-shadow "1px 0px 3px 0px gray" :padding-bottom "20px"}}
    [:div {:style {:padding-bottom "1em"}}
     [filter-input]]
    [:div {:style {:font-size "0.7em"}}
     [breadcrumbs]]]
   ;; tags to add/remove
   [general-cloud]
   
   ;; changes to submit
   [:div.mik-flush-right.padded-as-button {:style {:margin-top "5em"}}
    [:a.pure-button {:on-click #(refe/dispatch [:edit-nodes/cancel-tagging])} [views-utils/icon "cancel"] " Cancel"]
    [:a.pure-button.pure-button-primary {:on-click #(refe/dispatch [:edit-nodes/submit-tagging])} [views-utils/icon "save"]   " Save"]]])
