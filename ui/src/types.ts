// types.ts
export interface Commenter {
    id: string;
    name: string;
    email: string;
    picture: string;
}

export interface Comment {
    commentId: number;
    entryId: number;
    body: string;
    commenter: Commenter;
    status: 'PENDING' | 'APPROVED' | 'REJECTED';
    createdAt: string;
}
