(ns ^:figwheel-always mystic.components
  (:require
   [rum.core :as rum]
   [mystic.core :as core :refer [model]]
   ))

(def min-nodes 2)
(def max-nodes 12)
(def mystic-r 190)
(def mystic-o {:x 200 :y 200})
(def t-steps 100)

(defn dot-coord [key theta]
  (+ (key mystic-o) (* mystic-r ((if (= key :x) Math.cos Math.sin) theta))))

(defn i->theta [n i]
  (/ (* 2 Math.PI i) n))

(defn thetas [n]
  (map #(i->theta n %) (range n)))

(defn ramp [at width x]
  (let [a (+ at width)]
    (cond (< x at) 0
          (< x (+ at width)) (/ (- x at) width)
          :else 1)))

(rum/defc dot [theta]
  [:circle {:stroke "#ffffff" :stoke-width 5 :fill "#CCCCCC" :r 4
            :cx (dot-coord :x theta)
            :cy (dot-coord :y theta)}])

(rum/defc n-slider < rum/cursored [model mystic-n]
  [:input {:type "range" :value @mystic-n :min min-nodes :max max-nodes
           :style {:width "100%"}
           :on-change #(swap! core/model assoc
                              :mystic-n (-> % .-target .-value))}])

(rum/defc t-slider < rum/static [model method t]
  [:input {:type "range" :value (* t-steps t) :min 0 :max t-steps
           :style {:width "100%"}
           :on-change #(swap! model
                              (fn [old] (assoc old
                                              :mystic-t (assoc (:mystic-t old)
                                                               (dec method) (/ (-> % .-target .-value) t-steps)))))}])

(rum/defc count-input < rum/cursored [model mystic-n]
  [:span.node-count
   [:span
    [:input.inp-default
     {:type "number"
      :min min-nodes :max max-nodes :value @mystic-n
      :on-change #(swap! model assoc :mystic-n (.parseInt js/Number (-> % .-target .-value)))}]
    "points"]
   (n-slider model (rum/cursor model [:mystic-n]))])

(rum/defc dots-on-circle < rum/cursored [mystic-n]
  [:g (map-indexed  #(rum/with-key (dot %2) %1) (thetas @mystic-n))])

(defn mean [a b] (/ (+ a b) 2))

(rum/defc chord [method theta1 theta2 t]
  (let [x2 (if (= method 1)
             (mean (dot-coord :x theta1) (dot-coord :x theta2))
             (dot-coord :x theta2))
        y2 (if (= method 1)
             (mean (dot-coord :y theta1) (dot-coord :y theta2))
             (dot-coord :y theta2))]

    [:line {:x1 (dot-coord :x theta1)
            :y1 (dot-coord :y theta1)
            :x2 (+ (* (dot-coord :x theta1) (- 1 t)) (* x2 t))
            :y2 (+ (* (dot-coord :y theta1) (- 1 t)) (* y2 t))
            :stroke "rgba(0,128,255,0.3)" ;"#08f"
            :stroke-width 2
            :marker-end (if (< t 1) "url(#arrow)" "none")
            }]))

(defn isCursor?
  "This at least distinguishes cursors from literal numbers .toString returns [object Object] for a Cursor"
  [x]
  (= (first (.toString x)) "["))

(rum/defc chords [mystic-n mystic-t method]
  (let [t (if (isCursor? mystic-t)
            (@mystic-t (dec  method)) 1)]
    [:g
     (for [i (range @mystic-n)
           j (range (if (#{1 2} method) @mystic-n (if (= method 4) (- @mystic-n i) (inc i))))
           :let [i-theta (i->theta @mystic-n i)
                 j-theta (i->theta @mystic-n j)
                 j-theta' (i->theta @mystic-n (dec (- @mystic-n j)))
                 ]
           ]
       (cond
         ;(#{1 2} method) (rum/with-key (chord i-theta j-theta t) [2 i j])
         (= method 1) (rum/with-key (chord method i-theta j-theta t) [1 i j])
         (= method 2) (rum/with-key (chord method i-theta j-theta t) [2 i j])
         (= method 3) (rum/with-key (chord method i-theta j-theta (ramp (/ (dec i) @mystic-n) (/ 1 @mystic-n) t)) [3 i j])
         (= method 4) (rum/with-key (chord method i-theta j-theta' (ramp (/ i @mystic-n) (/ 1 @mystic-n) t)) [4 i j])))]))

(rum/defc draw [model method t-cursor]
  [:#methods
   [:div {:key 1
          :style {:margin-top "10px"}} "Draw"]
   [:div {:key 2}
    (t-slider model method @t-cursor)]])

(rum/defc basic-mystic-rose < rum/cursored rum/cursored-watch [model]
  [:div
   (count-input model (rum/cursor model [:mystic-n]))
   [:svg {:view-box "0 0 400 400"}
    [:g
     [:circle.outlined {:fill "none" :stroke "black" :stroke-width 2 :cx 200 :cy 200 :r 190}]
     (dots-on-circle (rum/cursor model [:mystic-n]))
     (chords (rum/cursor model [:mystic-n]) 1 2)]]
])

(rum/defc mystic-rose
  [model method]
  [:div {:style {:display "inline-block" :width "50%"}}
   [:strong (str "Method " method)]
   [:svg {:view-box "0 0 400 400"}
    [:defs
     [:marker {:id "arrow"
               :view-box "-0.5 -0.5 1 1"
               }
      [:circle {:cx 0 :cy 0 :r 0.3 :fill "black"}]]]
    [:g
     [:circle.outlined {:fill "none" :stroke "black" :stroke-width 2 :cx 200 :cy 200 :r 190}]
     (dots-on-circle (rum/cursor model [:mystic-n]))
     (chords (rum/cursor model [:mystic-n]) (rum/cursor model [:mystic-t]) method)
     ]]])

(rum/defc side-by-side < rum/cursored rum/cursored-watch [model]
  [:div
   (draw model 1 (rum/cursor model [:mystic-t 0]))
   (draw model 2 (rum/cursor model [:mystic-t 1]))
   (draw model 3 (rum/cursor model [:mystic-t 2]))
   (draw model 4 (rum/cursor model [:mystic-t 3]))
   (count-input model (rum/cursor model [:mystic-n]))
   [:div {:style {:clear "both"}}
    (mystic-rose model 1) (mystic-rose model 2)]
   [:div (mystic-rose model 3) (mystic-rose model 4)]])


(rum/defc stars < rum/cursored rum/cursored-watch [model method]
  [:div {:style {:display "inline-block" :width "100%"}}
   (draw model (rum/cursor model [:mystic-t]))
   (count-input model (rum/cursor model [:mystic-n]))
   [:svg {:view-box "0 0 400 400"}
    [:defs
     [:marker {:id "arrow"
               :view-box "-1 -1 2 2"
               }
      [:circle {:cx -2 :cy -2 :r 10 :fill "black"}]]]
    [:g
     [:circle.outlined {:fill "none" :stroke "black" :stroke-width 2 :cx 200 :cy 200 :r 190}]
     (dots-on-circle (rum/cursor model [:mystic-n]))
     (chords (rum/cursor model [:mystic-n]) (rum/cursor model [:mystic-t]) method)
     ]]])
