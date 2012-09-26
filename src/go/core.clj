(ns go.core
  (:refer-clojure :exclude [==])
  (:use clojure.core.logic))

(defn dead-equals [self vert hor]
  (== (vert 0) (vert 1))
  (== (hor 0) (hor 1))
  (== (hor 0) (vert 0))
  (== (vert 0) self))

(defn dead-half-equals [self vert hor]
  (== (vert 0) (vert 1))
  (== (hor 0) (hor 1))
  (== (vert 0) self))

(defn dead-vert-unequal [self vert hor]
  (!= (vert 0) (vert 1))
  (conde [
          (== (vert 0) self)
          (== (vert 1) self)]))

(defn dead-hor-unequal [self vert hor]
  (!= (hor 0) (hor 1))
  (conde [
          (== (hor 0) self)
          (== (hor 1) self)]))

(defn dead2 [self vert hor]
  (conde
    [
    (dead-equals self vert hor)
    (dead-vert-unequal self vert hor)
    (dead-hor-unequal self vert hor)]))

(def board
  [[:e :e :e :e :e :e :e]
   [:e :s :s :s :s :s :e]
   [:e :s :s :b :b :s :e]
   [:e :s :b :w :w :b :e]
   [:e :s :b :w :w :b :e]
   [:e :s :s :b :b :s :e]
   [:e :e :e :e :e :e :e]])

;A piece is alive if there is a rectilinear path
;to a space from it; there is a rectilinear path
;to a space from it either if it is adjacent to 
;a space on some side, or there is a rectilinear path
;to a space from one of its neighbors.

(defn right [[x y]] 
  [(inc x), y])

(defn left [[x y]]
  [(dec x), y])

(defn top [[x y]]
  [x, (dec y)])

(defn bottom [[x y]]
  [x, (inc y)])

(defn color [self]
  ((board (self 1)) (self 0)))

(defn alive [type self]
  (conde 
    ((== (color self) :e) u#)
    ((!= (color self) type) u#)
    ((== (color self) :s) s#)
    (s# (conde 
      ((alive type (right self)))
      ((alive type (left self)))
      ((alive type (top self)))
      ((alive type (bottom self)))))))

(defn blocking? [blockee blocker]
  (conde
    ((== (color blockee) :e) u#)
    ((== (color blocker) :e))
    ((!= (color blocker) (color blockee)))
    (:else (dead blocker))))
  
(defn dead [self]
  (all 
    (blocking? self (right self))
    (blocking? self (left self))
    (blocking? self (top self))
    (blocking? self (bottom self))))

(defn keep? [typee item]      
  (run 1 [q]
        (alive typee item)
        (== true q)))
       


