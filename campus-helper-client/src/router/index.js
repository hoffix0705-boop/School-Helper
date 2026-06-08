import { createRouter, createWebHistory } from "vue-router"

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
    children: [
      {
        path: "dashboard",
        name: "Dashboard",
        component: () => import("@/views/Dashboard.vue"),
        meta: { title: "控制台" },
      },
      {
        path: "tasks",
        name: "TaskList",
        component: () => import("@/views/task/List.vue"),
        meta: { title: "任务管理" },
      },
      {
        path: "tasks/create",
        name: "TaskCreate",
        component: () => import("@/views/task/Form.vue"),
        meta: { title: "发布任务" },
      },
      {
        path: "tasks/:id",
        name: "TaskDetail",
        component: () => import("@/views/task/Detail.vue"),
        meta: { title: "任务详情" },
      },
      {
        path: "tasks/:id/edit",
        name: "TaskEdit",
        component: () => import("@/views/task/Form.vue"),
        meta: { title: "编辑任务" },
      },
      {
        path: "categories",
        name: "CategoryList",
        component: () => import("@/views/category/List.vue"),
        meta: { title: "分类管理" },
      },
      {
        path: "users",
        name: "UserList",
        component: () => import("@/views/user/List.vue"),
        meta: { title: "用户管理" },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  document.title = to.meta?.title ? `${to.meta.title} - 校园帮` : "校园帮"
  next()
})

export default router
