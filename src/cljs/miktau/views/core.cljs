(ns miktau.views.core
  (:require [miktau.views.utils :as views-utils]
            [re-frame.core :as refe]
            [clojure.string :as clojure-string]
            [miktau.utils :as utils]
            [miktau.lorem :as lorem]))

(defn log-item [item]
  (do
    (.log js/console (str item))
    item))
(defn e->content
  [e]
  (str
   (aget e "target" "value" )))

(defn datetime-widget
  []
  [:div.pure-g
   [:div.pure-u-3-8
    [:select
     (for [i (range 1990 2019)]
       [:option i])]]
   [:div.pure-u-4-8
    [:select
     (for [i (range ["January" "February" "March"  "April"  "May"  "June" "July" "August" "September" "October" "November" "December"])]
       [:option i])]]
   [:div.pure-u-1-8
    [:select
     (for [i (range 1 32)]
       [:option i])]]])

(defn menu
  [first-item items]
  [:div.dropdown.padded-as-button
   first-item
   [:div.dropdown-content
    (for [[item pos] (zipmap items (range))]
      [:a.pure-button.mik-flush-left {:key pos :href "#" :style {:text-align "left"}} item])]])

(defn table-menu
  [first-item items]
  [:div.dropdown
   first-item
   (into
    []
    (concat
     [:div.dropdown-content {:style {:font-size "0.8em"}}]
     ;; it is highly unlikely that this menu is going to be
     ;; dynamic. Thus, simple counter as a key suffice
     items))])

(defn group-open []
  [:div
   (table-menu 
    [:span  (views-utils/icon "folder_open") "Open them"]
    [
     [:a.pure-button {:key "open in a single folder" :href "#" :on-click #(refe/dispatch [:file-operation :in-folder])}
      (views-utils/icon "folder_open") "In a single folder"]
     [:a.pure-button  {:key "each individually" :href "#" :on-click #(refe/dispatch [:file-operation :individually])}
      (views-utils/icon "list") "Each individually"]
     [:a.pure-button  {:key "individually" :href "#"  :on-click #(refe/dispatch [:file-operation :default-program])}
      (views-utils/icon "filter") "Each in default program"]])])

(defn selection-cloud []
  [:div 
   (for [tag  @(refe/subscribe [:selection-cloud])]
     [:a.tag.padded-as-button
      {:key (:key-name tag)
       :href "#"
       :on-click #(refe/dispatch [:clicked-cloud-item (:key-name tag)])
       :class
       (str
        (cond (:selected? tag) "selected"
              (:can-select? tag) "can-select"
              :else "disabled"))
       :style
       {:font-weight "300"
        :text-decoration "none"}}
      (:name tag) " "])])

(defn general-cloud []
  [:div
   (for [item @(refe/subscribe [:cloud])]
     [:div {:key (:group-name item)}
      [:div.mik-flush-right
       [:h2.padded-as-button.mik-cut-bottom.mik-cut-top.header-font.light-gray
        {:style {:padding-bottom "0px" :padding-top "0px"}}
        (:group-name item)]]
      (for [tag  (:group item)]
        [:a.tag.padded-as-button
         {:key  (:key-name tag)
          :href "#"
          :on-click
          (if (:disabled? tag)
            #(refe/dispatch [:clicked-disabled-cloud-item (tag :key-name)])
            #(refe/dispatch [:clicked-cloud-item (tag :key-name)]))
          :class
          (str
           (cond (:selected? tag) "selected"
                 (:can-select? tag) "can-select"
                 (:disabled? tag) "disabled"))
          :style
          {:font-size
           (str  (+ 0.6 (* 2.4  (tag :weighted-size))) "em")
           :font-weight "300"
           :text-decoration "none"}}
         (:name tag) " "])])])

(defn facet-group-select-time-subwidget
  [icon-name group-name additional-item-classes items]
  [:div.pure-g {:role "group"}
   [:h2.pure-u-1.mik-cut-bottom.mik-cut-top.padded-as-button.light-gray.header-font
    (views-utils/icon icon-name) group-name]
   [:div.pure-u-1
    (for [item items]
      [:a.pure-button {:key   (str (item :key-name))
                       :style {:text-align "left" :cursor "pointer"}
                       :on-click
                       (if (:disabled? item)
                         #(refe/dispatch [:clicked-disabled-calendar-item (:group item) (item :key-name)])
                         #(refe/dispatch [:click-on-calendar-item (:group item) (item :key-name)]))
                       :class
                       (str
                        (cond (:selected? item) "selected"
                              (:can-select? item) "can-select"
                              :else "disabled")
                        " " additional-item-classes)}
       (:name item)])]])


(defn  facet-group-select-time []
  (let [calendar @(refe/subscribe [:calendar])]
    [:div.pure-g
     ;; fast selection
     ;; (facet-group-select-time-subwidget
     ;;  "timeline" "Filter on" ""
     ;;  @(refe/subscribe [:fast-access-calendar]))
     
     ;; year      
     (facet-group-select-time-subwidget
      "line_style"
      (:group-name (:year calendar))
      " pure-u-1-5 tag "
      (:group (:year calendar)))
     ;; month
     (facet-group-select-time-subwidget
      "date_range"
      (:group-name (:month calendar))
      " pure-u-1-5 tag "
      (:group (:month calendar)))
     ;; day
     (facet-group-select-time-subwidget
      "date_range"
      (:group-name (:day calendar))
      " pure-u-1-5 tag"
      (:group (:day calendar)))]))

(defn found-group
  []
  [:div
   [:div.mik-flush-right {:style {:text-align "right"}}
    [:a.pure-button {:href "#"} (views-utils/icon "file_upload") " Add files "]]
   [:h2.mik-cut-bottom.mik-cut-top.header-font
    {:style {:font-size "3em"}} "Found " [:b " 10 "] " files"]
   [:a.unstyled-link {:href "#"} "All"
    [:span {:style {:font-size "1em" :color "gray"}} "&raquo;"]]
   (for [i (butlast (lorem/random-word-random-size 10))]
     [:a.unstyled-link {:href "#"}
      i
      [:span {:style {:font-size "1em" :color "gray"}} "&raquo;"]])
   [:a.unstyled-link {:href "#"}
    (first (lorem/random-word))]])

(defn tagging-now-group []
  (let [nodes-changing @(refe/subscribe [:nodes-changing])]
    [:div.background-1.padded-as-button {:style {}}
     [:div.mik-flush-left
      (views-utils/icon "photo_size_select_small")
      "Selected " [:b (:total-amount nodes-changing)] " files"
      [:a.unstyled-link {:href "#" :on-click #(refe/dispatch [:unselect-all-nodes])} " Unselect"]]
     (group-open)
     [:h2.header-font.light-gray.mik-cut-bottom.mik-flush-right
      (views-utils/icon "local_offer")
      "Remove tags from selection"]
     [:input {:name "tags-to-delete"
              :placeholder "Remove"
              :on-change #(refe/dispatch [:delete-tags-from-selection (e->content %)])
              :value
              (clojure-string/join "," (nodes-changing :tags-to-delete))}]
     [:h2.header-font.light-gray.mik-cut-bottom.mik-flush-right
      (views-utils/icon "local_offer")
      "Add new tags" ]
     [:input {:name "tags-to-add" :placeholder "Add"
              :on-change #(refe/dispatch [:add-tags-to-selection (e->content %)])
              :value (nodes-changing :tags-to-add)}]
     [:div.mik-flush-right {:style {:margin-top "2em"}}
      [:a.pure-button {:href "#"
                       :on-click #(refe/dispatch [:submit-tagging-now])}
       (views-utils/icon "save")
       "Save changes!"]
      [:a.pure-button {:href "#"
                       :on-click #(refe/dispatch [:cancel-tagging-now])}
       (views-utils/icon "cancel")
       "Cancel changes!"]]]))

(defn radio-button
  [text on-change selected?]
  (let [id (str (random-uuid))]
    [:label.pure-checkbox
     {:for id :style {:position "relative"}}
     [:input
      (if selected?
        {:id id  :style {:width "25px" :height "25px" :cursor "pointer"} :checked true :type "checkbox" :on-change on-change}
        {:id id  :style {:width "25px" :height "25px" :cursor "pointer"}               :type "checkbox" :on-change on-change})]
     [:span {:style {:padding-bottom "5px"}}
      text]]))

(defn file-table []
  (let [node-items  @(refe/subscribe [:node-items])]
    [:div.padded-as-button.background-1 
     [:div.pure-g {:style {:padding-bottom "1em"}}
      ;; select all nodes button
      [:div.pure-u-2-24
       (views-utils/position-absolute
        {:top "0px"}
        (radio-button "" #(refe/dispatch [:select-all-nodes]) (:all-selected? node-items)))]
      [:div.pure-u-6-24 
       (table-menu "Name"
                   [[:a.pure-button {:href "#" :key "order-a-z" :on-click #(refe/dispatch [:sort "name"])}
                     (views-utils/icon-rotated 180 "sort") " Order A-Z"]
                    [:a.pure-button {:href "#" :key "order-z-a" :on-click #(refe/dispatch [:sort "-name"])}
                     (views-utils/icon "sort")     " Order Z-A"]])]
      
      [:div.pure-u-10-24]
      [:div.pure-u-6-24.mik-flush-right
       (table-menu "Modified"
                   [
                    [:a.pure-button {:href "#" :key "recent" :on-click #(refe/dispatch [:sort "modified"])} "Recent"]
                    [:a.pure-button {:href "#" :key "older" :on-click #(refe/dispatch [:sort "-modified"])} "Older"]])]]
     [:div
      (for [node (:nodes node-items)]
        [:div.pure-g.table-hover
         {:key (str  (node :id))
          :style {:padding-bottom "10px"
                  :padding-top "10px"
                  :border-bottom "solid 1px #e3e3e3"
                  :cursor "pointer"}}
         [:div.pure-u-2-24.mik-flush-left
          (views-utils/position-absolute
           {:top ""}
           (radio-button "" #(refe/dispatch [:select-node (node :file-path) (node :name)]) (:selected? node)))]
         [:div.pure-u-6-24
          [:a.unstyled-link
           {:href "#" :style {:font-weight "300"}} (:name node)]]
         [:div.pure-u-10-24
          [:div 
           (for [tag (:tags node)]
             [:a.unstyled-link.gray
              {:key (:key-name tag)
               :href "#"
               :on-click #(refe/dispatch [:clicked-cloud-item (tag :key-name)])
               :class
               (str
                (cond (:to-delete? tag) "diff-delete-overline"
                      (:to-add?    tag) "diff-add"
                      :else
                      "")
                " "
                (cond
                  (:selected? tag) "selected"
                  (:can-select? tag) "can-select"
                  :else "disabled"))
               
               :style {:font-weight "300"}}  (:name tag) " "])
           (if-not (empty? (:all-tags node))
             [:a.unstyled-link
              {:href "#"
               :on-click #(refe/dispatch [:clicked-many-cloud-items (:all-tags node)])}
              "all"]
             [:span])]]
         [:div.pure-u-6-24.mik-flush-right
          [:a.unstyled-link {:href "#"
                             :on-click
                             #(refe/dispatch  [:click-on-calendar-item "FastAccess" (node :modified)])
                             :style
                             {:font-weight "300"}}
           (str
            (utils/pad (:year   (node :modified)) 4 "0") "."
            (utils/pad (:month  (node :modified)) 2 "0") "."
            (utils/pad (:day  (node :modified))   2 "0"))]]])]
     [:div.mik-flush-right.gray
      "Truncated: "
      [:b  (:omitted-nodes node-items)]]]))

(defn dropzone []
  [:div.mik-flush-center.background-1
   {:style {:padding "5em" :margin-top "1em" :margin-bottom "1em"
            :border "dashed 1px gray"}}
   [:a.pure-button {:href "#"}
    (views-utils/icon "file_upload")
    "Drop files here"]])

(defn filter-input []
  (let [filtering (refe/subscribe [:filtering])]
    [:div.padded-as-button {:style {:position "relative"}}
     [:input.background-0
      {:type "text" :placeholder "Filter"
       :value @filtering
       :on-change #(refe/dispatch [:filtering (e->content %)])
       :on-blur  #(refe/dispatch [:filtering ""])
       :style {:width "97%" :height "2em" :padding-left "20px" :background "white !important"}}]
     [:div {:style {:position "absolute" :right "30px" :top "23px"}}
      [:a.unstyled-link {:href "#" :on-click #(refe/dispatch [:clear])} 
       "Clear"]]
     [:div {:style {:position "absolute" :right "90px" :top "23px"}}
      (views-utils/icon "search")]]))


(defn drill
  []
  [:div.background-2
   [:div.pure-g
    [:div.pure-u-1-2
     ;; header
     [:div.pure-u-1.background-1
      [:div.pure-u-1-8
       [:div
        [:a.unstyled-link
         {:href "#"
          :on-click #(refe/dispatch [:back])
          :style
          {:font-size "3em", :padding-top "0.5em",
           :padding-left "0.5em", :padding-right "0.5em"}}
         (views-utils/icon "keyboard_arrow_left")]]]
      [:div.pure-u-7-8
       (filter-input)]]
     
     ;; time facet
     [:div.pure-u-1-3.background-0
      [:div.padded-as-button
       (facet-group-select-time)]]
     
     ;; cloud facet
     [:div.pure-u-2-3
      ;; [:div.background-1 {:style {:overflow "hidden" :height "70px"}}
      ;;  [:div {:style {:font-size "0.7em", :padding-bottom "2em"}}
      ;;   (selection-cloud)]]
      
      [:div.background-2
       [:div 
        (general-cloud)
        ]]]]
    
    ;; found, files, and tag anew group
    [:div.pure-u-1-2.background-3
     [:div.padded-as-button
      ;; (found-group)
      (file-table)]]
    ;; will be able to see it only when selected
    [:div {:style {:position "fixed", :bottom "0px", :right "0px", :width "100%"}}
     [:div.pure-u-1
      ;; (tagging-now-group)
      ]]]])

(defn choose-root
  []
  [:div
   {:style {:padding "5em", :border "dashed 1px gray"}}
   [:div.mik-flush-center.background-1
    [:a.padded-as-button.unstyled-link  {:href "#" :style {:font-size "4em"}}
     "Choose root directory"]
    [:div.mik-flush-center.background-1
     {:style
      {:padding "5em", :margin-top "1em", :margin-bottom "1em", :border "dashed 1px gray"}}
     [:a.pure-button {:href "#"}
      (views-utils/icon "file_upload")
      "Or Drop it here"]]]])

(defn processing
  []
  [:div.mik-flush-center.background-1
   {:style {:padding "5em" :border "dashed 1px gray"}}
   [:div
    [:a.pure-button {:href "#" :style {:font-size "4em"}}
     "Processing..."]
    [:div.mik-flush-center.background-1
     [:img {:src "/loading.gif"}]]]])

(defn main []
  (drill))
