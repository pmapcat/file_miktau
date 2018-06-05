(ns miktau.cloud.views
  (:require [miktau.generic.views-utils :as views-utils]
            [miktau.autocomplete.views :as autocomplete-views]
            [miktau.breadcrumbs.views :as breadcrumbs-views]
            [re-frame.core :as refe]))

(defn e->content
  [e]
  (str
   (aget e "target" "value" )))

(defn general-cloud-tag-item [tag]
  [:div.tag
   {:key  (:key-name tag)
    :on-click #(refe/dispatch  (:on-click tag))
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
   (for [tag  @(refe/subscribe [:cloud/cloud])]
     [:span {:key (:key-name tag)}
      [general-cloud-tag-item tag]])])

(defn general-meta-panel []
  [:div
   (for [[group items] @(refe/subscribe [:cloud/meta-cloud])]
     ^{:key group}
     [:div
      [:h3.mik-cut-bottom.light-gray {:style {:font-size "0.5em"}} group]
      (for [item items]
        ^{:key (:name item)}
        [:div.tag
         {:key (:name item)
          :on-click #(refe/dispatch (:on-click item))
          :style {:display "block" :font-size "0.6em" :padding-left "10px"}
          :class
          (str
           (cond (:selected? item) "selected"
                 (:can-select? item) "can-select"
                 (:disabled? item) "disabled"))}
         (:name item) " " [:span {:style {:font-weight "900"}} "[" (:size item ) "]"]])])])

(defn filter-input []
  [:div.pure-g.padded-as-button
   [:div.pure-u-7-8
    [autocomplete-views/filter-input [:cloud/get-app-data] false]]
     [:div.pure-u-1-8.mik-flush-right
      [:div.pure-button.pure-button-primary  {:style {:width "80%"}}
       [:div {:on-click #(refe/dispatch [:cloud/open-file-selecting-dialog])} "Add files"]]]])

(defn top-drawer []
  [:div.top-drawer.padded-as-button {:style {:font-size "0.7em"}}
   [:div.pure-u-1-24]
   [:div.pure-u-23-24
    [:span.unstyled-link "Current root is: "]
    [:a.red-clickable.unstyled-link  "[../some-current-root-dir/]"]
    [:div.pure-button.pure-button-primary.mik-flush-right {:style {:font-size "0.7em" :display "inline-block" :margin-left "10px"}} [:b "Change root"]]
    [:input.pure-button.pure-button-primary.mik-flush-right {:style {:font-size "0.7em" :display "inline-block" :margin-left "10px"} :type "file" }]]])


(defn back-button
  []
  [:div {:style {:position "relative"}}
   (if-not @(refe/subscribe [:undos?])
     [:a.unstyled-link.light-gray
      {:style
       {:font-size "5em" :cursor "default"}}
      [views-utils/icon "keyboard_arrow_left"]]
     [:a.unstyled-link.black-clickable
      {:on-click #(refe/dispatch [:undo])
       :style
       {:font-size "5em"}}
      [views-utils/icon "keyboard_arrow_left"]])])

(defn nodes-selected-view []
  (let [nodes-selection @(refe/subscribe [:cloud/nodes-selection])]
    [:div {:style {:font-size "0.8em" }}
     ;; selected
     [:div.pure-u-1-2 
      [:h2.mik-cut-top.light-gray  "Group actions on"]
      [:span "Selected: " [:b (:amount nodes-selection)] " files"]]
     
     ;; node items, narrow down
     [:div.pure-u-1-2.mik-flush-right
      [:button.pure-button.pure-button-primary
       {:on-click #(refe/dispatch (get-in nodes-selection [:narrow-results :on-click]))}
       (get-in nodes-selection [:narrow-results :name])] [:br]
      
      ;; each file action available
      (for [item (:links nodes-selection)]
        ^{:key (:name item)}
        [:span
         (if (:disabled? item)
           [:a.unstyled-link.blue-disaabled (:name item)]
           [:a.unstyled-link.blue-clickable {:on-click #(refe/dispatch (:on-click item))} (:name item )])
         [:br]])]]))

(defn main
  []
  [:div.pure-g {:style {:margin-bottom "200px"}}
   ;; header
   [:div.pure-u-1 {:style {:box-shadow "1px 1px 2px 0px gray"}}
    [top-drawer]
    [:div.pure-u-1-24
     [back-button]]
    [:div.pure-u-23-24
     [filter-input]
     [:div.padded-as-button {:style {:font-size "0.7em" :padding-bottom "1em"}}
      [breadcrumbs-views/breadcrumbs [:cloud/get-app-data]]]]]
   
   [:div.pure-u-1
    [:div.pure-u-1-8
     [:div.padded-as-button
      [general-meta-panel]]]
    [:div.pure-u-7-8
     [:div.padded-as-button
      [general-cloud]]]
    
    
    [:div.padded-as-button {:style {:position "fixed" :left "0px" :right "0px" :bottom "0px"  :background "white" :box-shadow "2px 0px 3px 0px grey"}}
     [nodes-selected-view]]]])
