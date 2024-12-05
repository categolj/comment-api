import React, {useEffect, useState} from 'react';
import {Comment} from './types';
import {Avatar} from "./retro-ui/Avatar.tsx";
import {Button} from "./retro-ui/Button.tsx";
import {Text} from "./retro-ui/Text.tsx";
import ReactTimeAgo from "react-time-ago";
import {Badge} from "./retro-ui/Badge.tsx";

export interface CommentTableProps {
    csrfToken: string
}

const CommentTable: React.FC<CommentTableProps> = ({csrfToken}) => {
    const [comments, setComments] = useState<Comment[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [statusFilter, setStatusFilter] = useState<string>('ALL');
    const [entryIdFilter, setEntryIdFilter] = useState<string>('');
    const [dialogContent, setDialogContent] = useState<string | null>(null);
    const [sortOrder, setSortOrder] = useState<'asc' | 'desc'>('asc');
    const [visibleEmails, setVisibleEmails] = useState<{ [id: string]: boolean }>({});

    const checkResponse = (response: Response) => {
        if (response.status === 302) {
            const location = response.headers.get('Location');
            if (location) {
                window.location.href = location;
            }
        } else if (response.status === 403) {
            setDialogContent('You are not allowed to manage comments!');
        }
    }

    useEffect(() => {
        const fetchComments = async () => {
            try {
                const response = await fetch('/admin/comments');
                checkResponse(response);
                if (!response.ok) {
                    throw new Error('Failed to fetch comments');
                }
                const data: Comment[] = await response.json();
                setComments(data);
            } catch (error) {
                console.error('Error fetching comments:', error);
            } finally {
                setLoading(false);
            }
        };
        fetchComments();
    }, []);

    const filteredComments = comments.filter((comment) => {
        return (
            (statusFilter === 'ALL' || comment.status === statusFilter) &&
            (entryIdFilter === '' || comment.entryId.toString().includes(entryIdFilter))
        );
    });

    const sortedComments = [...filteredComments].sort((a, b) => {
        const dateA = new Date(a.createdAt).getTime();
        const dateB = new Date(b.createdAt).getTime();
        return sortOrder === 'asc' ? dateA - dateB : dateB - dateA;
    });

    const updateStatus = async (id: number, status: 'PENDING' | 'APPROVED' | 'REJECTED') => {
        try {
            const response = await fetch(`/admin/comments/${id}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': csrfToken,
                },
                body: JSON.stringify({status}),
            });
            checkResponse(response);
            if (!response.ok) {
                throw new Error('Failed to update status');
            }

            setComments((prev) =>
                prev.map((comment) =>
                    comment.commentId === id ? {...comment, status} : comment
                )
            );
        } catch (error) {
            console.error('Error updating status:', error);
        }
    };

    const deleteComment = async (id: number) => {
        try {
            const response = await fetch(`/admin/comments/${id}`, {
                method: 'DELETE',
                headers: {
                    'X-CSRF-TOKEN': csrfToken,
                }
            });
            checkResponse(response);
            if (!response.ok) {
                throw new Error('Failed to delete comment');
            }
            setComments((prev) => prev.filter((comment) => comment.commentId !== id));
        } catch (error) {
            console.error('Error deleting comment:', error);
        }
    };

    const toggleEmailVisibility = (id: string) => {
        setVisibleEmails((prev) => ({
            ...prev,
            [id]: !prev[id],
        }));
    };


    if (loading) {
        return <div className="text-center text-gray-500 mt-10">Loading comments...</div>;
    }

    const statusColor = {
        PENDING: "bg-yellow-300 text-yellow-800 border-yellow-800",
        APPROVED: "bg-green-300 text-green-800 border-green-800",
        REJECTED: "bg-red-300 text-red-800 border-red-800"
    };

    return (
        <>
            {dialogContent && (
                <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
                    <div className="bg-white p-6 shadow-lg max-w-lg w-full border-2 border-black shadow-md">
                        <Text as={"h4"} className="text-xl font-bold mb-4">Comment</Text>
                        <p className="text-gray-700 p-4">{dialogContent}</p>
                        <Button onClick={() => setDialogContent(null)}>Close</Button>
                    </div>
                </div>
            )}
            <div className="mb-4 flex gap-4">
                <select
                    value={statusFilter}
                    onChange={(e) => setStatusFilter(e.target.value)}
                    className="px-4 py-2 border-2 border-black shadow-md transition focus:outline-none focus:shadow-xs"
                >
                    <option value="ALL">All Statuses</option>
                    <option value="PENDING">Pending</option>
                    <option value="APPROVED">Approved</option>
                    <option value="REJECTED">Rejected</option>
                </select>
                <input
                    type="text"
                    value={entryIdFilter}
                    onChange={(e) => setEntryIdFilter(e.target.value)}
                    placeholder="Filter by Entry ID"
                    className="px-4 py-2 border-2 border-black shadow-md transition focus:outline-none focus:shadow-xs"
                />
            </div>
            <table className="table-auto w-full border-collapse border border-black shadow-md">
                <thead>
                <tr className="bg-primary-400 hover:bg-primary-500">
                    <th className="border border-black px-4 py-2">ID</th>
                    <th className="border border-black px-4 py-2">Entry ID</th>
                    <th className="border border-black px-4 py-2">Comment</th>
                    <th className="border border-black px-4 py-2">Commenter</th>
                    <th className="border border-black px-4 py-2 cursor-pointer"
                        onClick={() => setSortOrder((prev) => (prev === 'asc' ? 'desc' : 'asc'))}>
                        Created At {sortOrder === 'asc' ? '↑' : '↓'}
                    </th>
                    <th className="border border-black px-4 py-2">Status</th>
                    <th className="border border-black px-4 py-2">Action</th>
                </tr>
                </thead>
                <tbody>
                {sortedComments.map((comment) => (
                    <tr key={comment.commentId} className="text-center">
                        <td className="border border-black px-4 py-2">{comment.commentId}</td>
                        <td className="border border-black px-4 py-2">{comment.entryId}</td>
                        <td className="border border-black px-4 py-2 cursor-pointer"
                            onClick={() => setDialogContent(comment.body)}>
                            {comment.body.length > 32
                                ? `${comment.body.slice(0, 32)}...`
                                : comment.body}
                        </td>
                        <td className="border border-black px-4 py-2">
                            <div className="flex items-center justify-center">
                                <Avatar>
                                    <Avatar.Image src={`${comment.commenter.picture}`}
                                                  alt={`${comment.commenter.name}`}/>
                                    <Avatar.Fallback>{`${comment.commenter.name}`}</Avatar.Fallback>
                                </Avatar>
                                <span
                                    onClick={() => toggleEmailVisibility(comment.commenter.id)}
                                    className="ml-2 cursor-pointer">
                                    {comment.commenter.name}
                                </span>
                                {visibleEmails[comment.commenter.id] && (
                                    <Badge className={"ml-2"}>
                                        {comment.commenter.email}
                                    </Badge>
                                )}
                            </div>
                        </td>
                        <td className="border border-black px-4 py-2">
                            <ReactTimeAgo date={new Date(comment.createdAt)} locale="en-US"/>
                        </td>
                        <td className="border border-black px-4 py-2">
                            <select
                                value={comment.status}
                                onChange={(e) =>
                                    updateStatus(comment.commentId, e.target.value as 'PENDING' | 'APPROVED' | 'REJECTED')
                                }
                                className={`px-4 py-2 w-full border-2 border-black shadow-md transition focus:outline-none focus:shadow-xs ${statusColor[comment.status]}`}
                            >
                                <option value="PENDING">Pending</option>
                                <option value="APPROVED">Approved</option>
                                <option value="REJECTED">Rejected</option>
                            </select>
                        </td>
                        <td className="border border-black px-4 py-2">
                            <Button
                                onClick={() => deleteComment(comment.commentId)}
                                className="bg-red-500 text-white hover:bg-red-600">
                                Delete
                            </Button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </>
    );
};

export default CommentTable;
