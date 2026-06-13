import { createRouter, createWebHistory } from "vue-router"
import { getToken, getUser } from "@/utils/storage"

export const MenuList = [
  {
    path: "/dashboard",
    name: "Dashboard",
    meta: { title: "控制台", icon: "DataBoard" },
    component: () => import("@/views/Dashboard.vue"),
  },
  {
    path: "/tasks",
    name: "TaskList",
    meta: { title: "任务管理", icon: "Tickets" },
    component: () => import("@/views/task/List.vue"),
  },
  {
    path: "/tasks/create",
    meta: { title: "发布任务", icon: "Plus", hidden: true },
    component: () => import("@/views/task/Form.vue"),
  },
  {
    path: "/tasks/:id",
    meta: { title: "任务详情", hidden: true },
    component: () => import("@/views/task/Detail.vue"),
  },
  {
    path: "/tasks/:id/edit",
    meta: { title: "编辑任务", hidden: true },
    component: () => import("@/views/task/Form.vue"),
  },
  {
    path: "/categories",
    name: "CategoryList",
    meta: { title: "分类管理", icon: "FolderOpened", roles: [0] },
    component: () => import("@/views/category/List.vue"),
  },
  {
    path: "/users",
    name: "UserList",
    meta: { title: "用户管理", icon: "User", roles: [0] },
    component: () => import("@/views/user/List.vue"),
  },
]

const routes = [
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/Login.vue"),
    meta: { title: "登录", noAuth: true },
  },
  {
    path: "/",
    component: () => import("@/views/Layout.vue"),
    redirect: "/dashboard",
    children: MenuList,
  },
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, _from, next) => {
  document.title = to.meta?.title ? `${to.meta.title} - 校园帮` : "校园帮"
  if (!to.meta.noAuth && !getToken()) {
    return next("/login")
  }
  const user = getUser()
  if (to.meta.roles && (!user || !to.meta.roles.includes(user.userType))) {
    return next("/dashboard")
  }
  next()
})

export default router
