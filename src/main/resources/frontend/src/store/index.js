import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user: JSON.parse(localStorage.getItem('user')) || null,
    isLoggedIn: !!localStorage.getItem('user')
  },
  
  mutations: {
    SET_USER(state, user) {
      state.user = user
      state.isLoggedIn = true
      localStorage.setItem('user', JSON.stringify(user))
    },
    
    UPDATE_USER(state, userData) {
      // 合并更新用户数据，只更新传入的字段
      if (state.user) {
        state.user = { ...state.user, ...userData }
        localStorage.setItem('user', JSON.stringify(state.user))
      }
    },
    
    CLEAR_USER(state) {
      state.user = null
      state.isLoggedIn = false
      localStorage.removeItem('user')
    }
  },
  
  actions: {
    login({ commit }, user) {
      commit('SET_USER', user)
    },
    
    updateUser({ commit }, userData) {
      commit('UPDATE_USER', userData)
    },
    
    logout({ commit }) {
      commit('CLEAR_USER')
    }
  },
  
  getters: {
    currentUser: state => state.user,
    isLoggedIn: state => state.isLoggedIn,
    isAdmin: state => {
      if (!state.user) return false
      // ftype === 1 表示管理员
      return state.user.ftype === 1
    }
  }
})
