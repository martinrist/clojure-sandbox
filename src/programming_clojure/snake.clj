(ns programming-clojure.snake
  (:import (java.awt Color Dimension Graphics)
           (javax.swing JPanel JFrame Timer JOptionPane)
           (java.awt.event ActionListener KeyListener KeyEvent))
  (:use programming_clojure.import-static)
)

(declare VK_LEFT VK_RIGHT VK_UP VK_DOWN paintComponent)

(import-static java.awt.event.KeyEvent VK_LEFT VK_RIGHT VK_UP VK_DOWN)

(def width 75)
(def height 50)
(def point-size 10)
(def turn-millis 75)
(def win-length 5)
(def dirs {VK_LEFT  [-1  0]
           VK_RIGHT [ 1  0]
           VK_UP    [ 0 -1]
           VK_DOWN  [ 0  1]})

(defn add-points
  [& pts]
  (vec (apply map + pts)))

(defn point-to-screen-rect
      [pt]
      (map #(* point-size %)
           [(pt 0) (pt 1) 1 1]))

(defn create-apple
  []
  {:location [(rand-int width) (rand-int height)]
   :colour (Color. 210 50 90)
   :type :apple})

(defn create-snake
  []
  {:body (list [1 1])
   :dir [1 0]
   :colour (Color. 15 160 70)
   :type :snake})

(defn move
  [{:keys [body dir] :as snake} & grow]
  (assoc snake :body (cons (add-points (first body) dir)
                           (if grow body (butlast body)))))

(defn win?
  [{body :body}]
  (>= (count body) win-length))

(defn head-overlaps-body?
  [{[head & body] :body}]
  (contains? (set body) head))

(def lose? head-overlaps-body?)

(defn eats?
  [{[snake-head] :body} {apple :location}]
  (= snake-head apple))

(defn turn
  [snake newdir]
  (assoc snake :dir newdir))

(defn reset-game
  [snake apple]
  (dosync (ref-set apple (create-apple))
          (ref-set snake (create-snake)))
  nil)

(defn update-direction
  [snake newdir]
  (when newdir
    (dosync (alter snake turn newdir))))

(defn update-positions
  [snake apple]
  (dosync
    (if (eats? @snake @apple)
      (do (ref-set apple (create-apple))
          (alter snake move :grow))
      (alter snake move)))
  nil)

(defn fill-point
  [g pt color]
  (let [[x y width height] (point-to-screen-rect pt)]
    (.setColor ^Graphics g color)
    (.fillRect ^Graphics g x y width height)))

(defmulti paint (fn [_ object & _] (:type object)))

(defmethod paint :apple
           [g {:keys [location colour]}]
  (fill-point g location colour))

(defmethod paint :snake
           [g {:keys [body colour]}]
  (doseq [point body]
    (fill-point g point colour)))

(defn game-panel
  [frame snake apple]

  (proxy [JPanel ActionListener KeyListener] []
    (paintComponent
      [^Graphics g]
      (proxy-super paintComponent g)
      (paint g @snake)
      (paint g @apple))

    (actionPerformed
      [_]
      (update-positions snake apple)
      (when (lose? @snake)
        (reset-game snake apple)
        (JOptionPane/showMessageDialog frame "You lose!"))
      (when (win? @snake)
        (reset-game snake apple)
        (JOptionPane/showMessageDialog frame "You win!"))
      (.repaint this))

    (keyPressed
      [e]
      (update-direction snake (dirs (.getKeyCode e))))

    (getPreferredSize
      []
      (Dimension. (* (inc width) point-size)
                  (* (inc height) point-size)))

    (keyReleased
      [_])

    (keyTyped
      [_])))

(defn game
  []
  (let [snake (ref (create-snake))
        apple (ref (create-apple))
        frame (JFrame. "Snake")
        panel (game-panel frame snake apple)
        timer (Timer. turn-millis panel)]
    (doto panel
      (.setFocusable true)
      (.addKeyListener panel))
    (doto frame
      (.add panel)
      (.pack)
      (.setVisible true))
    (.start timer)
    [snake, apple, timer]))