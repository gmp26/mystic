(ns ^:figwheel-always mystic.cards

  (:require
   [rum.core :as rum]
   [mystic.core :as core]
   [mystic.components :as comp])

  (:require-macros
   [devcards.core :as dc :refer [defcard deftest]]
   ))

(enable-console-print!)

(defcard devcards-ok
  [:div
   [:h1 "Devcards ok!"]])

(defcard svg-container
  (comp/mystic-rose core/model))


;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html
