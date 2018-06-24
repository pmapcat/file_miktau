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
         (:name item) " " [:span.mik-float-right   (:size item )]])])])

(defn filter-input []
  [:div.pure-g
   [:div.pure-u-1
    [autocomplete-views/filter-input [:cloud/get-app-data] false {:placeholder "Type tags in here…"}]]])

(defn top-drawer []
  
  [:div.top-drawer.padded-as-button {:style {:font-size "0.7em"}}
   [:div.pure-u-1-24]
   [:div.pure-u-23-24
    [:span.unstyled-link "Current root is: "]
    [:a.red-clickable.unstyled-link  "[../some-current-root-dir/]"]
    [:div.pure-button.pure-button-primary.mik-flush-right {:style {:font-size "0.7em" :display "inline-block" :margin-left "10px"}} [:b "Change root"]]
    [:input.pure-button.pure-button-primary.mik-flush-right {:style {:font-size "0.7em" :display "inline-block" :margin-left "10px"}
                                                             :on-change
                                                             (fn [e]
                                                               (.log js/console e))
                                                             :type "file"}]]])

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

(defn cloud-no-files-view
  []
  [:div.padded-as-button.light-gray
   [:h1 {:style {:font-size "3em"}} "Nothing here, yet" ]
   [:p "You can start by"]
   [:ul
    [:li.padded-as-button [:a.pure-button.pure-button-primary "Choosing folder"] " with more files"]
    [:li.padded-as-button [:a.pure-button.pure-button-primary "Adding new"] " files to this folder"]
    [:li.padded-as-button "Drag & drop some files here"]]])

(defn empty-cloud-view []
  [:div.padded-as-button.light-gray.mik-cut-top
   [:h1.mik-cut-top {:style {:font-size "3em"}} "There are no categories" ]
   [:p "You probably want to add some by "]
   [:ul
    [:li "Clicking on " ]
    [:li "Edit tags on selection"]
    [:li "In the right bottom corner"]]])

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
;; @(refe/subscribe [:cloud/empty-view])
(defn main
  []
  (let [emptiness @(refe/subscribe [:cloud/empty-view])]
    [:div.pure-g {:style {:margin-bottom "200px"}}
     ;; header
     [:div.pure-u-1 {:style {:box-shadow "#dedede 1px 1px 2px 0px"}}
      (if (:show-filtering-view emptiness)
        [:span
         [top-drawer]
         [:div.pure-u-1-12
          [back-button]]
         [:div.pure-u-11-12
          [filter-input]
          [:div.padded-as-button {:style {:font-size "0.7em" :padding-bottom "1em"}}
           [breadcrumbs-views/breadcrumbs [:cloud/get-app-data]]]]])]
     
     (if (:show-empty-all emptiness)
       [:div.pure-u-1
        [cloud-no-files-view]]
       [:span])
     
     [:div.pure-u-1
      [:div.pure-u-1-8
       [:div.padded-as-button
        [general-meta-panel]]]
      [:div.pure-u-7-8
       [:div.padded-as-button
        [general-cloud]
        (if (:show-empty-cloud emptiness)
          [:div.pure-u-1
           [empty-cloud-view]]
          [:span])]]
      (if (:show-bottom-view emptiness)
        [:div.padded-as-button {:style {:position "fixed" :left "0px" :right "0px" :bottom "0px"  :background "white" :box-shadow "2px 0px 3px 0px grey"}}
         [nodes-selected-view]]
        [:span]
        )]]))
