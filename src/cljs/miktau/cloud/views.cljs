(ns miktau.cloud.views
  (:require [miktau.generic.views-utils :as views-utils]
            [re-frame.core :as refe]))

(defn e->content
  [e]
  (str
   (aget e "target" "value" )))

(defn general-cloud-tag-item [tag]
  [:a.tag
   {:key  (:key-name tag)
    :href "#"
    :on-click
    (if (:disabled? tag)
      #(refe/dispatch [:cloud/clicked-disabled-cloud-item (tag :key-name)])
      #(refe/dispatch [:cloud/clicked-cloud-item (tag :key-name)]))
    :class
    (str
     (cond (:selected? tag) "selected"
           (:can-select? tag) "can-select"
           (:disabled? tag) "disabled"))
    :style
    {:font-size
     (str  (+ 0.6 (* 2.4  (tag :weighted-size))) "em")}}
   (:name tag) " "])

(defn general-cloud []
  [:div
   (for [tag  (:group (first @(refe/subscribe [:cloud/cloud])))]
     [:span {:key (:key-name tag)}
      [general-cloud-tag-item tag]])])


(defn general-tree []
  [:div
   (for [tag @(refe/subscribe [:cloud/general-tree])]
     [:a.tag
      {:key  (str  (:key-name tag) (:pad-level tag))
       :href "#"
       :on-click
       (if (:disabled? tag)
         #(refe/dispatch [:cloud/clicked-disabled-cloud-item (tag :key-name)])
         #(refe/dispatch [:cloud/clicked-cloud-item (tag :key-name)]))
       :class
       (str
        (cond (:selected? tag) " selected "
              (:can-select? tag) " can-select "
              (:disabled? tag) " disabled ")
        (if (:header? tag)
          " padded-as-button mik-cut-bottom mik-cut-top header-font "
          " ")
        " " (:pad-background-class tag))
       :style
       {:font-size "1em" :display "block"
        :margin-top "3px"
        :margin-bottom "3px"
        :margin-left (str (:pad-level tag) "em")}}
      " "(:name tag) ])])

(defn facet-group-select-time-subwidget
  [icon-name group-name additional-item-classes items]
  [:div.pure-g {:role "group"}
   [:h2.pure-u-1.mik-cut-bottom.mik-cut-top.padded-as-button.light-gray.header-font
    [views-utils/icon icon-name] group-name]
   [:div.pure-u-1
    (for [item items]
      [:a.mik-flush-center {:key   (str (item :key-name))
           :style {:cursor "pointer" :display "inline-block"}
           :on-click
           (if (:disabled? item)
             #(refe/dispatch [:cloud/clicked-disabled-calendar-item (:group item) (item :key-name)])
             #(refe/dispatch [:cloud/click-on-calendar-item (:group item) (item :key-name)]))
           :class
           (str
            (cond (:selected? item) "selected"
                  (:can-select? item) "can-select"
                  :else "disabled")
            " " additional-item-classes)}
       (:name item)])]])

(defn  facet-group-select-time []
  (let [calendar @(refe/subscribe [:cloud/calendar])]
    [:div.pure-g
     ;; year      
     [facet-group-select-time-subwidget
      "line_style"
      (:group-name (:year calendar))
      " pure-u-1-3 tag "
      (:group (:year calendar))]
     ;; month
     [facet-group-select-time-subwidget
      "date_range"
      (:group-name (:month calendar))
      " pure-u-1-5 tag "
      (:group (:month calendar))]
     ;; day
     [facet-group-select-time-subwidget
      "date_range"
      (:group-name (:day calendar))
      " pure-u-1-5 tag"
      (:group (:day calendar))]]))

(defn filter-input []
  (let [filtering (refe/subscribe [:cloud/filtering])]
    [:div.padded-as-button {:style {:position "relative"}}
     [:input.background-0
      {:type "text" :placeholder "Filter"
       :value @filtering
       :on-change #(refe/dispatch [:cloud/filtering (e->content %)])
       :style {:width "97%" :height "2em" :padding-left "20px" :background "white !important"}}]
     [:div {:style {:position "absolute" :right "30px" :top "23px"}}
      [:a.unstyled-link {:href "#" :on-click #(refe/dispatch [:cloud/clear])} 
       "Clear"]]
     [:div {:style {:position "absolute" :right "90px" :top "23px"}}
      [views-utils/icon "search"]]]))

(defn main
  []
  [:div.pure-g
   ;; header
   [:div.pure-u-1
    [:div.pure-u-1-8
     [:div
      ;; [:a.unstyled-link
      ;;  {:href "#"
      ;;   :on-click #(refe/dispatch [:back])
      ;;   :style
      ;;   {:font-size "3em", :padding-top "0.5em",
      ;;    :padding-left "0.5em", :padding-right "0.5em"}}
      ;;  [views-utils/icon "keyboard_arrow_left"]]
      ]]
    [:div.pure-u-7-8
     [filter-input]]]
   ;; time facet
   [:div.pure-u-1-5.background-0 [:div.padded-as-button [facet-group-select-time]]]
   ;; tree facet
   [:div.pure-u-2-5.background-1 [:div.padded-as-button [general-tree]]]
   ;; cloud facet
   [:div.pure-u-4-5 [:div.background-2 [:div  [general-cloud]]]]])
