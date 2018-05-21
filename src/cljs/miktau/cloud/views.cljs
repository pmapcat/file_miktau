(ns miktau.cloud.views
  (:require [miktau.generic.views-utils :as views-utils]
            [reagent.core :as reagent]
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
   (for [tag  @(refe/subscribe [:cloud/cloud])]
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
    [:div.pure-g {:style {:font-size "1em"}}
     ;; year      
     [facet-group-select-time-subwidget
      "line_style"
      (:group-name (:year calendar))
      " pure-u-1-5 tag "
      (:group (:year calendar))]
     ;; month
     [facet-group-select-time-subwidget
      "date_range"
      (:group-name (:month calendar))
      " pure-u-1-8 tag "
      (:group (:month calendar))]
     ;; day
     [facet-group-select-time-subwidget
      "date_range"
      (:group-name (:day calendar))
      " pure-u-1-8 tag"
      (:group (:day calendar))]]))

(defn filter-input []
  [:div.padded-as-button {:style {:position "relative" :padding-top "20px" :padding-bottom "20px"}}
   [:input
    {:type "text" :placeholder "Type tags in here..."
     :style {:width "90%" :height "2em" :padding-left "3px" :background "white !important"}}]
   [:div.pure-button.pure-button-primary {:style {:position "absolute" :right "30px" :top "20px"}}
    [views-utils/icon "search"]]])


(defn for-every-and-last
  [data-set]
  (let [last-by-index (dec (count data-set))]
    (for [[v k] (map list (range) data-set)]
      [k {:point k
          :index v
          :last? (= v last-by-index)
          :first? (= v 0)}])))

(defn breadcrumbs []
  (let [breadcrumbs @(refe/subscribe [:cloud/breadcrumbs])]
    [:div 
     [:a.unstyled-link.red-clickable {:href "#" :on-click #(refe/dispatch [:cloud/clear]) :style {:padding-right "5px"}} "«Clear»"]
     
     [:span {:style {:padding-right "5px"}}
      
      ;; calendar
      (for [[item meta-item]  (for-every-and-last (:calendar breadcrumbs))]
        ^{:key (:name item)}
        [:span [:a.unstyled-link.black-clickable {:href "#" :on-click #(refe/dispatch (:on-click item))} (:name item)]
         (if-not (:last? meta-item) " > " " | ")])
      
      ;; cloud items
      (for [[item meta-item] (for-every-and-last (:cloud-items breadcrumbs))]
        ^{:key (:name item)}
        [:span [:a.unstyled-link.black-clickable {:href "#" :on-click #(refe/dispatch (:on-click item))} (:name item)]
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
           [:a.unstyled-link.green-clickable {:href "#" :on-click #(refe/dispatch (:on-click item))} (:name item)]
           (if-not (:last? meta-item) " • " " ")])
        
        (if (:can-expand? breadcrumbs)
          [:a.unstyled-link.green-clickable {:href "#" :style {:padding-left "5px" } :on-click #(refe/dispatch [:cloud/breadcrumbs-show-all?-switch])} "…" ]
          [:span])
        " )"]
       [:span])]))



(defn back-button
  []
  [:div {:style {:position "relative"}}
   [:a.unstyled-link 
    {:href "#"
     :on-click #(refe/dispatch [:back])
     :style
     {:font-size "3em", :padding-top "0.2em",
      :position "absolute"
      :padding-left "0.5em", :padding-right "0.5em"}}
    [views-utils/icon "keyboard_arrow_left"]]])

(defn nodes-selected-view []
  [:div {:style {:font-size "0.8em" }}
   [:div.pure-u-1-2 
    [:h2.mik-cut-top.light-gray "Group actions on"]
    [:span "Selected: " [:b "47"] " files"]]
   [:div.pure-u-1-2.mik-flush-right
    [:button.pure-button.pure-button-primary
     "Narrow results"] [:br]
    
    [:a.unstyled-link.blue-clickable {:href "#"}
     "Edit tags on selection"] [:br]
    [:a.unstyled-link.blue-clickable {:href "#"}
     "Open in a single folder"] [:br]
    [:a.unstyled-link.blue-clickable {:href "#"}
     "Open each individually"] [:br]
    [:a.unstyled-link.blue-clickable {:href "#"}
     "Open each in a default program"]]])

(defn main
  []
  [:div.pure-g {:style {:margin-bottom "200px"}}
   ;; header
   [:div.pure-u-1 {:style {:box-shadow "1px 1px 2px 0px gray"}}
    [:div.pure-u-1-4
     [:div [back-button]]]
    [:div.pure-u-3-4
     [filter-input]]]
   ;; [:div.pure-u-1-8
   ;;  [:button.pure-button.default {:style {:background "blue"}}
   ;;   [views-utils/icon "search"]]]
   
   [:div.pure-u-1-4
    [:div.padded-as-button [facet-group-select-time]]]
   [:div.pure-u-3-4
    ;; header panel
    

    ;; breadcrumbs
    [:div.pure-u-1.padded-as-button {:style {:padding-bottom "30px" :font-size "0.9em"}}
     [breadcrumbs]]
    
    ;; cloud
    [:div.pure-u-1.padded-as-button
     [general-cloud]]
    [:div.padded-as-button {:style {:position "fixed" :left "0px" :right "0px" :bottom "0px"  :background "white" :box-shadow "2px 0px 3px 0px grey"}}
     [nodes-selected-view]]
    ]])
