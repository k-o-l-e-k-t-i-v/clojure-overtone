;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone
(defonce cbus (control-bus))

(defsynth csynth [rate 10 bus 0]
  (let [trig (impulse:kr rate)
        step (stepper:kr trig :min 1 :max 32)]
    (send-trig:kr trig step)
    (out:kr bus trig)))

(def csynth-2 (csynth :bus cbus :rate 2.5))
(ctl csynth-2 :rate 0)
(ctl csynth-1 :rate 0)
(kill csynth-2)
(def csynth-1 (csynth :bus cbus :rate 10))
(stop)
(definst boom
  [freq 30
   div  1
   tbus 0
   adec 0.3
   fdec 0.1
   facc 300]
  (let [time (pulse-divider:kr (in:kr tbus) div)
        amp  (decay time adec)
        fenv (+ freq (* facc (decay time fdec)))
        sig  (sin-osc fenv)
        sig  (* sig amp)
        ]
    (out 0 (pan2 sig 0))))

(def k1 (boom :tbus cbus))
(ctl k1 :div 4)

(def k2 (boom :tbus cbus :div 6 :freq 50))
(kill boom)
(def k4 (boom :tbus cbus :div 14 :freq 180))
(ctl k4 :freq 60)

(ctl k4 :div 4)
(ctl k3 :div 5)
(ctl k2 :div 7)
(ctl k1 :freq 400)
(ctl csynth-1 :rate 5)
;(inst-fx! boom fx-reverb)
;(ins)

(ctl csynth-1 :rate 5)

(def notes-1 (buffer 10))
(def notes-2 (buffer 14))
(buffer-write! notes-1 (take 10 (cycle (chord :Db4 :11 0))))
(buffer-write! notes-2 (take 14 (cycle (shuffle (chord :Gb4 :7 -1)))))
(definst melos
  [nbus 0
   tbus 0
   div 1]
  (let [time (pulse-divider:kr (in:kr tbus) div)
        note-pos (mod (pulse-count:kr time) (buf-frames nbus))
        note (buf-rd:kr 1 nbus note-pos)
        freq (midicps note)
        sig  (sin-osc freq)
        amp  (decay time 0.5)
        sig  (* sig amp)
        sig  (free-verb sig 0.7 0.92 0.2)]
    (out 0 (pan2 sig))))

(def m1 (melos :tbus cbus :div 6 :nbus notes-1))
(def m2 (melos :tbus cbus :div 4 :nbus notes-1))
(def m3 (melos :tbus cbus :div 8 :nbus notes-2))
(def m4 (melos :tbus cbus :div 6 :nbus notes-2))
(kill m2)

(demo 2 (sin-osc 100))
(do
  (ctl m1 :div 30)
  (ctl m2 :div 50)
  (ctl m3 :div 90)
  (ctl m4 :div 130))
