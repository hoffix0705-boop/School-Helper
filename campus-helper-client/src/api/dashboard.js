import request from "@/api/request"

export function getDashboardStats() {
  return request.get("/dashboard/stats")
}

export function getRecentTasks() {
  return request.get("/dashboard/recent-tasks")
}