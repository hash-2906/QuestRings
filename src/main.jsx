import React, { useEffect, useMemo, useRef, useState } from 'react'
import { createRoot } from 'react-dom/client'
import { motion, AnimatePresence } from 'framer-motion'
import confetti from 'canvas-confetti'
import { Flame, Plus, Check, X, Dumbbell, Brain, Target, Droplets, BookOpen, Moon, Timer, Sparkles, ChevronRight, RotateCcw, Zap, Trophy, CalendarDays, ListTodo, Clock, Trash2 } from 'lucide-react'
import './styles.css'

const categories = { 
    Vitality: { color: '#FF2D55', icon: Dumbbell }, 
    Intellect: { color: '#30D158', icon: Brain }, 
    Discipline: { color: '#0A84FF', icon: Target } 
}
const iconMap = { Dumbbell, Brain, Target, Droplets, BookOpen, Moon, Timer, Sparkles }
const starter = [
    { id: 'water', category: 'Vitality', title: 'Hydrate', goal: 'Drink 3L water', icon: 'Droplets', done: false },
    { id: 'workout', category: 'Vitality', title: 'Move your body', goal: '45-min workout', icon: 'Dumbbell', done: false },
    { id: 'pages', category: 'Intellect', title: 'Read & grow', goal: 'Read 20 pages', icon: 'BookOpen', done: false },
    { id: 'focus', category: 'Intellect', title: 'Deep focus', goal: '60-min distraction free', icon: 'Brain', done: false },
    { id: 'morning', category: 'Discipline', title: 'Morning ritual', goal: 'Complete your routine', icon: 'Sparkles', done: false },
    { id: 'sleep', category: 'Discipline', title: 'Sleep on time', goal: 'In bed by 11:00 PM', icon: 'Moon', done: false },
]
const initial = { habits: starter, tasks: [], xp: 540, level: 7, streak: 12, history: [45, 75, 100, 62, 82, 100, 0] }
const storage = 'questrings-v1'
function getSaved() { try { return { ...initial, ...JSON.parse(localStorage.getItem(storage)) } } catch { return initial } }

function Ring({ value, radius, width, color, delay, isFocused, isAnyFocused }) { 
    const circumference = 2 * Math.PI * radius; 
    const targetOffset = circumference * (1 - Math.min(100, Math.max(0, value)) / 100);
    const opacity = isAnyFocused ? (isFocused ? 1 : 0.45) : 1;
    const strokeW = isFocused ? width + 2 : width;

    return (
        <motion.circle 
            cx="150" 
            cy="150" 
            r={radius} 
            fill="none" 
            stroke={color} 
            strokeWidth={strokeW} 
            strokeLinecap="round" 
            strokeDasharray={circumference} 
            initial={false}
            animate={{ 
                strokeDashoffset: targetOffset,
                opacity: opacity,
                strokeWidth: strokeW
            }} 
            transition={{ duration: 0.6, ease: 'easeOut', delay }} 
        /> 
    );
}

function App() {
    const [data, setData] = useState(getSaved)
    const [active, setActive] = useState('All')
    const [view, setView] = useState('habits')
    const [modal, setModal] = useState(false)
    const [levelUp, setLevelUp] = useState(false)
    const [notice, setNotice] = useState('')
    const [now, setNow] = useState(Date.now())
    const closed = useRef(new Set())

    useEffect(() => localStorage.setItem(storage, JSON.stringify(data)), [data])
    useEffect(() => { const timer = setInterval(() => setNow(Date.now()), 1000); return () => clearInterval(timer) }, [])

    const groups = useMemo(() => Object.fromEntries(Object.keys(categories).map(c => [c, data.habits.filter(h => h.category === c)])), [data.habits])
    const progress = useMemo(() => Object.fromEntries(Object.keys(categories).map(c => { const list = groups[c] || []; return [c, list.length ? Math.round(list.filter(x => x.done).length / list.length * 100) : 0] })), [groups])
    const overall = data.habits.length ? Math.round(data.habits.filter(h => h.done).length / data.habits.length * 100) : 0
    const xpPercent = Math.min(100, ((data.xp % 1000) / 1000) * 100)
    const date = new Intl.DateTimeFormat('en-US', { weekday: 'long', month: 'short', day: 'numeric' }).format(new Date())
    const days = Array.from({ length: 7 }, (_, i) => { const day = new Date(); day.setDate(day.getDate() - 6 + i); return { label: new Intl.DateTimeFormat('en-US', { weekday: 'narrow' }).format(day), num: day.getDate() } })

    function celebrate(color) { confetti({ particleCount: 65, spread: 60, origin: { y: .62 }, colors: [color, '#fff', '#d8d8e5'] }) }

    function toggle(id) { 
        const habit = data.habits.find(h => h.id === id); 
        if (!habit) return;
        const becameDone = !habit.done; 
        const nextHabits = data.habits.map(h => h.id === id ? { ...h, done: becameDone } : h); 
        let gain = becameDone ? 25 : -25; 
        let msg = becameDone ? '+25 XP earned' : ''; 
        if (becameDone) { 
            const list = nextHabits.filter(h => h.category === habit.category); 
            if (list.every(h => h.done) && !closed.current.has(habit.category)) { 
                closed.current.add(habit.category); 
                gain += 100; 
                celebrate(categories[habit.category].color); 
                msg = `${habit.category} ring closed! +125 XP` 
            } 
        } 
        let xp = Math.max(0, data.xp + gain); 
        let level = data.level; 
        if (xp >= 1000) { 
            xp -= 1000; 
            level += 1; 
            setLevelUp(true); 
            setTimeout(() => setLevelUp(false), 2600) 
        } 
        setData(d => ({ 
            ...d, 
            habits: nextHabits, 
            xp, 
            level, 
            history: [...d.history.slice(0, 6), Math.round(nextHabits.filter(h => h.done).length / nextHabits.length * 100)] 
        })); 
        if (msg) { 
            setNotice(msg); 
            setTimeout(() => setNotice(''), 2100) 
        } 
    }

    function reset() { 
        closed.current.clear(); 
        setData(d => ({ ...d, habits: d.habits.map(h => ({ ...h, done: false })), history: [...d.history.slice(0, 6), 0] })); 
        setNotice('Fresh quest day started') 
    }

    function addHabit(e) { 
        e.preventDefault(); 
        const form = new FormData(e.currentTarget); 
        const title = form.get('title').trim(); 
        if (!title) return; 
        const cat = form.get('category');
        setData(d => ({ ...d, habits: [...d.habits, { id: Date.now().toString(), title, goal: form.get('goal') || 'Complete this quest', category: cat, icon: form.get('icon'), done: false }] })); 
        setModal(false); 
        setActive(cat);
    }

    function addTask(e) { 
        e.preventDefault(); 
        const form = new FormData(e.currentTarget); 
        const title = form.get('taskTitle').trim(); 
        const dueAt = new Date(`${form.get('taskDate')}T${form.get('taskTime')}`).getTime(); 
        if (!title || !dueAt || dueAt <= Date.now()) return; 
        setData(d => ({ ...d, tasks: [...d.tasks, { id: Date.now().toString(), title, notes: form.get('taskNotes') || 'Scheduled quest', createdAt: Date.now(), dueAt, done: false }] })); 
        setModal(false); 
        setNotice('Task scheduled'); 
        setTimeout(() => setNotice(''), 2100);
    }

    function taskState(task) { 
        const remaining = Math.max(0, task.dueAt - now); 
        const day = 86400000; 
        const total = Math.max(day, task.dueAt - task.createdAt); 
        const percent = task.done ? 100 : remaining <= day ? remaining / day * 100 : Math.min(100, remaining / total * 100); 
        const hours = Math.floor(remaining / 3600000); 
        const minutes = Math.floor(remaining % 3600000 / 60000); 
        const seconds = Math.floor(remaining % 60000 / 1000); 
        return { percent, countdown: remaining <= day, label: remaining === 0 ? 'Time is up' : remaining <= day ? `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}` : `${Math.ceil(remaining / day)}d remaining` } 
    }

    function formatDue(timestamp) { 
        return new Intl.DateTimeFormat('en-US', { weekday: 'short', month: 'short', day: 'numeric', hour: 'numeric', minute: '2-digit' }).format(new Date(timestamp)) 
    }

    const displayedHabits = active === 'All' ? data.habits : (groups[active] || [])
    const centerPercent = active === 'All' ? overall : (progress[active] || 0)
    const centerLabel = active === 'All' ? 'COMPLETE' : `${active.toUpperCase()}`

    return (
        <main className="app-shell">
            <div className="noise" />
            <header>
                <div className="brand">
                    <span className="brand-mark"><Zap size={18} /></span>
                    <span>Quest<span>Rings</span></span>
                </div>
                <nav className="main-nav">
                    <button className={view === 'habits' ? 'selected' : ''} onClick={() => setView('habits')}>
                        <Sparkles size={15} />Daily rings
                    </button>
                    <button className={view === 'tasks' ? 'selected' : ''} onClick={() => setView('tasks')}>
                        <ListTodo size={15} />Tasks <small>{data.tasks.filter(task => !task.done).length}</small>
                    </button>
                </nav>
                <div className="date"><CalendarDays size={15} />{date}</div>
            </header>

            <section className="topbar">
                <div>
                    <p className="eyebrow">CURRENT LEVEL</p>
                    <h2>Lvl {data.level} <span>Guardian</span></h2>
                </div>
                <div className="xp-wrap">
                    <div className="xp-label">
                        <span>Total Experience</span>
                        <b>{data.xp.toLocaleString()} / 1,000 XP</b>
                    </div>
                    <div className="xp-track">
                        <motion.div className="xp-fill" animate={{ width: `${xpPercent}%` }} />
                    </div>
                </div>
                <div className="streak">
                    <div className="flame"><Flame size={23} fill="currentColor" /></div>
                    <div><b>{data.streak} day streak</b><small>Keep the fire alive</small></div>
                </div>
            </section>

            {/* Persistent Hero Concentric Rings: Always dynamic, never disappears */}
            <section className="hero">
                <div className="hero-copy">
                    <span className="pill"><Sparkles size={14} /> DAILY QUEST</span>
                    <h1>Make today<br /><em>legendary.</em></h1>
                    <p>Small wins compound. Close your rings and become the hero of your own story.</p>
                    <button onClick={() => document.querySelector('.quests')?.scrollIntoView({ behavior: 'smooth' })}>
                        View today’s quests <ChevronRight size={17} />
                    </button>
                </div>
                <div className="rings-wrap">
                    <svg viewBox="0 0 300 300" className="rings">
                        {/* Colored background tracks so rings are always visible */}
                        <circle cx="150" cy="150" r="122" fill="none" stroke="#FF2D5528" strokeWidth="18" />
                        <circle cx="150" cy="150" r="92" fill="none" stroke="#30D15828" strokeWidth="18" />
                        <circle cx="150" cy="150" r="62" fill="none" stroke="#0A84FF28" strokeWidth="18" />
                        <g transform="rotate(-90 150 150)">
                            <Ring value={progress.Vitality} radius={122} width={18} color="#FF2D55" delay={0} isFocused={active === 'Vitality'} isAnyFocused={active !== 'All'} />
                            <Ring value={progress.Intellect} radius={92} width={18} color="#30D158" delay={0.05} isFocused={active === 'Intellect'} isAnyFocused={active !== 'All'} />
                            <Ring value={progress.Discipline} radius={62} width={18} color="#0A84FF" delay={0.1} isFocused={active === 'Discipline'} isAnyFocused={active !== 'All'} />
                        </g>
                    </svg>
                    <div className="ring-center">
                        <b>{centerPercent}%</b>
                        <span>{centerLabel}</span>
                    </div>
                    <div className="ring-legend">
                        <button className={active === 'All' ? 'active' : ''} onClick={() => setActive('All')}>
                            <i style={{ background: '#7E5DFF' }} />All {overall}%
                        </button>
                        <button className={active === 'Vitality' ? 'active' : ''} onClick={() => setActive('Vitality')}>
                            <i style={{ background: '#FF2D55' }} />Vitality {progress.Vitality}%
                        </button>
                        <button className={active === 'Intellect' ? 'active' : ''} onClick={() => setActive('Intellect')}>
                            <i style={{ background: '#30D158' }} />Intellect {progress.Intellect}%
                        </button>
                        <button className={active === 'Discipline' ? 'active' : ''} onClick={() => setActive('Discipline')}>
                            <i style={{ background: '#0A84FF' }} />Discipline {progress.Discipline}%
                        </button>
                    </div>
                </div>
            </section>

            <section className="calendar-card">
                <div>
                    <p className="eyebrow">WEEKLY MOMENTUM</p>
                    <h3>Every day tells a story.</h3>
                </div>
                <div className="week">
                    {days.map((day, i) => (
                        <div className={'day ' + (i === 6 ? 'today' : '')} key={i}>
                            <span>{day.label}</span>
                            <div className="mini-ring" style={{ '--p': `${data.history[i] || 0}%` }}>
                                <b>{day.num}</b>
                            </div>
                        </div>
                    ))}
                </div>
            </section>

            {view === 'habits' ? (
                <section className="quests">
                    <div className="section-head">
                        <div>
                            <p className="eyebrow">YOUR QUESTS</p>
                            <h2>Build your legend.</h2>
                        </div>
                        <button className="add" onClick={() => setModal(true)}>
                            <Plus size={17} /> Add habit
                        </button>
                    </div>

                    {/* Category Filter Tabs including All Habits */}
                    <div className="tabs">
                        <button className={active === 'All' ? 'active' : ''} onClick={() => setActive('All')} style={{ '--c': '#7E5DFF' }}>
                            <Sparkles size={17} />All habits<small>{data.habits.filter(h => h.done).length}/{data.habits.length}</small>
                        </button>
                        {Object.entries(categories).map(([name, category]) => { 
                            const Icon = category.icon; 
                            return (
                                <button className={active === name ? 'active' : ''} onClick={() => setActive(name)} key={name} style={{ '--c': category.color }}>
                                    <Icon size={17} />{name}<small>{(groups[name] || []).filter(h => h.done).length}/{(groups[name] || []).length}</small>
                                </button>
                            );
                        })}
                    </div>

                    <div className="habit-list">
                        {displayedHabits.map((habit, index) => { 
                            const Icon = iconMap[habit.icon] || Target; 
                            return (
                                <motion.article layout initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: index * .04 }} className={'habit ' + (habit.done ? 'done' : '')} key={habit.id}>
                                    <div className="habit-icon" style={{ color: categories[habit.category]?.color || '#7E5DFF', background: (categories[habit.category]?.color || '#7E5DFF') + '19' }}>
                                        <Icon size={20} />
                                    </div>
                                    <div className="habit-text">
                                        <h3>{habit.title}</h3>
                                        <p>{habit.goal}</p>
                                    </div>
                                    <span className="xp-chip">+25 XP</span>
                                    <button aria-label={'Complete ' + habit.title} className="check" onClick={() => toggle(habit.id)} style={{ '--c': categories[habit.category]?.color || '#7E5DFF' }}>
                                        {habit.done ? <Check size={18} /> : <span />}
                                    </button>
                                </motion.article>
                            );
                        })}
                    </div>
                </section>
            ) : (
                <section className="tasks">
                    <div className="section-head">
                        <div>
                            <p className="eyebrow">UPCOMING TASKS</p>
                            <h2>Make time count.</h2>
                        </div>
                        <button className="add" onClick={() => setModal(true)}>
                            <Plus size={17} /> Add task
                        </button>
                    </div>
                    {data.tasks.length === 0 ? (
                        <div className="empty-tasks">
                            <ListTodo size={28} />
                            <h3>No scheduled tasks yet.</h3>
                            <p>Give your next important thing a time and place.</p>
                        </div>
                    ) : (
                        <div className="task-list">
                            {data.tasks.slice().sort((a, b) => a.dueAt - b.dueAt).map(task => { 
                                const state = taskState(task); 
                                return (
                                    <motion.article layout className={'task-card ' + (task.done ? 'done' : '')} key={task.id}>
                                        <div className="task-card-top">
                                            <div>
                                                <span className="task-kicker"><Clock size={13} />{formatDue(task.dueAt)}</span>
                                                <h3>{task.title}</h3>
                                                <p>{task.notes}</p>
                                            </div>
                                            <button className="task-delete" aria-label={'Delete ' + task.title} onClick={() => setData(d => ({ ...d, tasks: d.tasks.filter(item => item.id !== task.id) }))}>
                                                <Trash2 size={16} />
                                            </button>
                                        </div>
                                        <div className="task-progress-meta">
                                            <span>{task.done ? 'Completed' : state.countdown ? 'COUNTDOWN' : 'TIME REMAINING'}</span>
                                            <b>{task.done ? 'Done' : state.label}</b>
                                        </div>
                                        <div className={'task-progress ' + (state.countdown ? 'countdown' : '')}>
                                            <motion.div animate={{ width: `${state.percent}%` }} transition={{ duration: .4 }} />
                                        </div>
                                        <button className="task-complete" onClick={() => setData(d => ({ ...d, tasks: d.tasks.map(item => item.id === task.id ? { ...item, done: !item.done } : item) }))}>
                                            {task.done ? <><Check size={16} /> Completed</> : <><span /> Mark complete</>}
                                        </button>
                                    </motion.article>
                                );
                            })}
                        </div>
                    )}
                </section>
            )}

            <AnimatePresence>
                {modal && (
                    <motion.div className="overlay" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}>
                        <motion.form className="modal" onSubmit={view === 'habits' ? addHabit : addTask} initial={{ scale: .94, y: 12 }} animate={{ scale: 1, y: 0 }} exit={{ scale: .94, y: 12 }}>
                            <button type="button" className="close" onClick={() => setModal(false)}><X size={19} /></button>
                            <span className="pill"><Plus size={14} /> {view === 'habits' ? 'NEW QUEST' : 'NEW TASK'}</span>
                            {view === 'habits' ? (
                                <>
                                    <h2>Create a habit</h2>
                                    <p>Add a small promise to your future self.</p>
                                    <label>Quest title<input name="title" autoFocus placeholder="e.g. Evening walk" required /></label>
                                    <label>Micro-goal<input name="goal" placeholder="e.g. Walk for 20 minutes" /></label>
                                    <div className="form-row">
                                        <label>Ring category
                                            <select name="category">
                                                {Object.keys(categories).map(x => <option key={x}>{x}</option>)}
                                            </select>
                                        </label>
                                        <label>Icon
                                            <select name="icon">
                                                {Object.keys(iconMap).map(x => <option key={x}>{x}</option>)}
                                            </select>
                                        </label>
                                    </div>
                                    <button className="create">Create quest <ChevronRight size={17} /></button>
                                </>
                            ) : (
                                <>
                                    <h2>Schedule a task</h2>
                                    <p>Give your next win a deadline.</p>
                                    <label>Task name<input name="taskTitle" autoFocus placeholder="e.g. Submit project brief" required /></label>
                                    <label>Notes<input name="taskNotes" placeholder="e.g. Send it to the team" /></label>
                                    <div className="form-row">
                                        <label>Date<input name="taskDate" type="date" min={new Date().toISOString().slice(0, 10)} required /></label>
                                        <label>Time<input name="taskTime" type="time" required /></label>
                                    </div>
                                    <button className="create">Schedule task <ChevronRight size={17} /></button>
                                </>
                            )}
                        </motion.form>
                    </motion.div>
                )}
            </AnimatePresence>

            <AnimatePresence>
                {(levelUp || notice) && (
                    <motion.div className={'toast ' + (levelUp ? 'levelup' : '')} initial={{ opacity: 0, y: 20, scale: .94 }} animate={{ opacity: 1, y: 0, scale: 1 }} exit={{ opacity: 0, y: 10 }}>
                        {levelUp ? <><Trophy size={20} /> Level Up! You’re now Lvl {data.level}</> : <><Sparkles size={17} />{notice}</>}
                    </motion.div>
                )}
            </AnimatePresence>
        </main>
    )
}

createRoot(document.getElementById('root')).render(<App />)
